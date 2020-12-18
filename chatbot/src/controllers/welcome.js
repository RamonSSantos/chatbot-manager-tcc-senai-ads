import User from '@/models/User'

export default async function welcome(agent) {
  const { firstName, userId, registration } = agent.parameters

  if (userId) {
    try {
      // Get User By Pk
      const userResponse = await User.findByPk(userId)
      const user = userResponse.dataValues

      if (user.registration === Number(registration) && user.status === 1) {
        // User authenticated and enabled
        agent.context.set({
          name: 'user-validation-followup',
          lifespan: 10,
          parameters: { firstName, userId: user.id, registration: user.registration },
        })
        agent.setFollowupEvent({ name: 'AuthenticatedUser' })
        agent.add('')
      } else {
        // Invalid user or user with status disabled/approval
        agent.context.set({
          name: 'chatbot-presentation-followup',
        })
        agent.setFollowupEvent({ name: 'ChatbotPresentation' })
        agent.add('')
      }
    } catch (error) {
      if (error.name === 'SequelizeConnectionRefusedError') {
        agent.add(`Não foi possível realizar a consulta!\n\n`
          + `Error name: ${error.name}\n\n`
          + `${error.parent}`)
      }
    }
  } else {
    // User is not authenticated
    agent.context.set({
      name: 'chatbot-presentation-followup',
    })
    agent.setFollowupEvent({ name: 'ChatbotPresentation' })
    agent.add('')
  }
}
