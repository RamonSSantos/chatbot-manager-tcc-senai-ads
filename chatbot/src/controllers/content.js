import Content from '@/models/Content'
import Message from '@/models/Message'
import Monitoring from '@/models/Monitoring'
import Question from '@/models/Question'
import Topic from '@/models/Topic'
import User from '@/models/User'
import MessageService from '@/services/MessageService'
import MonitoringService from '@/services/MonitoringService'
import createHangoutsCard from '@/utils/createHangoutsCard'
import createPayload from '@/utils/createPayload'

export default async function sendContent(agent) {
  const { userId, firstName } = agent.parameters
  try {
    // setTimeout
    const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

    // Get User By Pk
    const userResponse = await User.findByPk(userId)
    const user = userResponse.dataValues

    // Get All Contents
    // Status 1 = ativo
    const contentResponse = await Content.findAll({
      where: { status: 1 },
      include: {
        all: true,
      },
    })
    const contents = contentResponse.map((item) => item.dataValues)

    const content = await contents.reduce(async (result, item) => {
      let contentId

      // Verifica os conteúdos que possuem o mesmo setor do usuário ou que não possuem setor
      if (item.sector_id === user.sector_id || item.sector_id === null) {
        contentId = item.id
      }

      if (contentId) {
        // Verifica a existência ou cria o id do conteúdo e do usuário na tabela monitoring
        // Status 1 = evento está na fila para ser enviado ao usuário
        const [monitoringResponse, created] = await Monitoring.findOrCreate({
          where: { user_id: user.id, content_id: contentId },
          defaults: {
            status: 1,
          },
        })

        const monitoring = monitoringResponse.dataValues
        if (created) {
          // Registro criado

          // Create log
          await wait(1000)
          await MonitoringService.createLog(monitoring.id, monitoring.status)

          const contentByFilterResponse = await Content.findOne({
            where: { id: monitoring.content_id },
            include: {
              all: true,
            },
          })
          result = contentByFilterResponse.dataValues
        } else if (monitoring.status === 1) {
          // Registro já existia (Aguardando ser enviado)
          const contentByFilterResponse = await Content.findOne({
            where: { id: monitoring.content_id },
            include: {
              all: true,
            },
          })
          result = contentByFilterResponse.dataValues
        }
      }

      return result
    }, Promise.resolve({}))

    // Não possui conteúdo para enviar
    if (Object.keys(content).length === 0) {
      agent.add('No momento não há nenhum conteúdo/material/aviso que possa ser enviado para você!')
    } else if (content.questions.length > 0) {
      // Existe perguntas para o conteúdo escolhido
      const questionsId = content.questions.map((item) => item.dataValues.id)

      let messageId

      // Verifica a existência ou cria mensagem com o actor 1 na tabela message
      // Actor 1 = Chatbot
      // Status 1 = Pergunta enviada para o usuário
      const questionId = await questionsId.reduce(async (result, item) => {
        const [messageResponse, created] = await Message.findOrCreate({
          where: { user_id: user.id, content_id: content.id, question_id: item },
          defaults: {
            actor: 1,
            status: 1,
          },
        })

        const message = messageResponse.dataValues
        messageId = message.id

        if (created) {
          // Registro criado

          // Create log (Enviado pelo chatbot)
          const question = 1
          await wait(1000)
          await MessageService.createLog(message.id, question, user.id)

          result = item
        } else {
          // Registro já existia (Aguardando ser enviado)

          result = message.question_id
        }

        return result
      }, 0)

      // Não existe mais perguntas para o conteúdo processado
      if (questionId === 0) {
        agent.add('No momento não há nenhum conteúdo/material/aviso que possa ser enviado para você!')
      } else {
        // Get Question By Pk
        const questionResponse = await Question.findByPk(questionId)
        const questionDescription = questionResponse.dataValues.description

        // Envia a pergunta para o usuário
        agent.context.set({
          name: 'content-followup',
          lifespan: 1,
          parameters: {
            firstName,
            userId: user.id,
            contentId: content.id,
            questionId,
            questionDescription,
            messageId,
          },
        })
        agent.setFollowupEvent({ name: 'SendQuestion' })
        agent.add('')
      }
    } else {
      // Enviando o conteúdo
      const topicResponse = await Topic.findByPk(content.topic_id)
      const topicDescription = topicResponse.dataValues.description

      const answerDescription = content.answers.map((answer) => answer.dataValues.description)
      const json = createHangoutsCard(
        content.title, topicDescription, answerDescription, content.attachment,
      )

      const payload = createPayload(json)

      // Update monitoring status
      // Status 2 = evento enviado
      await wait(1000)
      await Monitoring.update({
        status: 2,
      }, {
        where: { user_id: user.id, content_id: content.id },
        returning: true,
        plain: true,
      })

      // Get monitoring updated
      // Status 2 = evento enviado
      const monitoringUpdatedResponse = await Monitoring.findOne({
        where: { user_id: user.id, content_id: content.id, status: 2 },
      })

      // Create log
      const monitoringUpdated = monitoringUpdatedResponse.dataValues
      await wait(1000)
      await MonitoringService.createLog(monitoringUpdated.id, monitoringUpdated.status)

      agent.add(payload)
    }
  } catch (error) {
    // TODO
    console.log(error)
  }
}
