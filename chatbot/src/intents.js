import { WebhookClient } from 'dialogflow-fulfillment'

import sendContent from '@/controllers/content'
import sendQuestion from '@/controllers/question'
import { getUser, userValidation } from '@/controllers/user'
import welcome from '@/controllers/welcome'

function webhookProcessing(request, response) {
  const agent = new WebhookClient({ request, response })

  const intentMap = new Map()
  intentMap.set('Welcome', welcome)
  intentMap.set('Get User', getUser)
  intentMap.set('User Validation', userValidation)
  intentMap.set('Content', sendContent)
  intentMap.set('Send Question', sendQuestion)

  agent.handleRequest(intentMap)
}

export default webhookProcessing
