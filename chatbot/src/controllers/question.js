import Content from '@/models/Content'
import Message from '@/models/Message'
import Monitoring from '@/models/Monitoring'
import Question from '@/models/Question'
import Topic from '@/models/Topic'
import MessageService from '@/services/MessageService'
import MonitoringService from '@/services/MonitoringService'
import createHangoutsCard from '@/utils/createHangoutsCard'
import createPayload from '@/utils/createPayload'
import sentimentAnalysis from '@/utils/sentiment'
import tries from '@/utils/tries'

export default async function sendQuestion(agent) {
  // setTimeout
  const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

  const {
    firstName, userId, contentId, questionId, getAnswer, messageId,
  } = agent.parameters

  // Update Message status
  // Status 2 = pergunta recebida
  await wait(1000)
  await Message.update({
    status: 2,
  }, {
    where: { id: messageId },
    returning: true,
    plain: true,
  })

  // Get Content by id
  const contentResponse = await Content.findOne({
    where: { id: contentId },
    include: {
      all: true,
    },
  })

  // Cria mensagem com o actor 2 (usuário)
  // Status 3 = pergunta respondida
  await wait(1000)
  const message = await Message.create({
    actor: 2,
    status: 3,
    user_id: userId,
    content_id: contentId,
    question_id: questionId,
  })

  // Create log (Enviado pelo usuário)
  const answer = 2
  await wait(1000)
  await MessageService.createLog(message.id, answer, userId)

  // Consulta todos as palavras-chaves do conteúdo
  const keywordsDescription = contentResponse.keywords.map(
    (keyword) => keyword.dataValues.description,
  )

  const stringMatch = tries(keywordsDescription, getAnswer)
  const sentiment = sentimentAnalysis(getAnswer)
  const stringMatchSentiment = tries(keywordsDescription, sentiment.words)

  let status
  let payload
  if (stringMatch.length > 0 || sentiment.score >= 2 || stringMatchSentiment.length > 0) {
    // Conteúdo pode ser encaminhado para o usuário após o processamento da resposta
    const topicResponse = await Topic.findByPk(contentResponse.topic_id)
    const topicDescription = topicResponse.dataValues.description

    const answerDescription = contentResponse.answers.map((item) => item.dataValues.description)
    const json = createHangoutsCard(
      contentResponse.title, topicDescription, answerDescription, contentResponse.attachment,
    )

    // Conteúdo enviado para o usuário
    status = 2
    payload = createPayload(json)
  } else {
    // Consulta todos as perguntas do conteúdo
    const questionsId = contentResponse.questions.map(
      (question) => question.dataValues.id,
    )

    let newMessageId
    const newQuestionId = await questionsId.reduce(async (result, item) => {
      if (questionId !== item) {
        // Verifica se ainda existe outras perguntas que podem ser enviadas para o usuário
        // Actor 1 = Chatbot
        // Status 1 = Pergunta enviada para o usuário
        const messageResponse = await Message.findOne({
          where: {
            actor: 1,
            status: 1,
            user_id: userId,
            content_id: contentId,
            question_id: item,
          },
        })

        // Existe ainda perguntas para enviar ao usuário desse mesmo conteúdo
        if (messageResponse !== null) {
          const newMessage = messageResponse.dataValues

          newMessageId = newMessage.id
          result = newMessage.question_id
        }
      }

      return result
    }, 0)

    if (newQuestionId === 0) {
      // Não possui mais perguntas para este conteúdo

      // Status 5 = Resposta do usuário não combinou com as palavras-chaves
      status = 5
      payload = 'Obrigado pela retorno! A sua resposta foi processada.'
    } else {
      // Get Question By Pk
      const questionResponse = await Question.findByPk(newQuestionId)
      const questionDescription = questionResponse.dataValues.description

      payload = null

      // Envia nova pergunta para o usuário
      agent.context.set({
        name: 'content-followup',
        lifespan: 1,
        parameters: {
          firstName,
          userId,
          contentId,
          questionId: newQuestionId,
          questionDescription,
          messageId: newMessageId,
        },
      })
      agent.setFollowupEvent({ name: 'SendQuestion' })
      agent.add('')
    }
  }

  if (payload !== null) {
    // Update monitoring status
    await wait(1000)
    await Monitoring.update({
      status,
    }, {
      where: { user_id: userId, content_id: contentId },
      returning: true,
      plain: true,
    })

    // Get monitoring updated
    const monitoringUpdatedResponse = await Monitoring.findOne({
      where: { user_id: userId, content_id: contentId, status },
    })

    // Create log
    const monitoringUpdated = monitoringUpdatedResponse.dataValues
    await wait(1000)
    await MonitoringService.createLog(monitoringUpdated.id, monitoringUpdated.status)

    agent.add(payload)
  }
}
