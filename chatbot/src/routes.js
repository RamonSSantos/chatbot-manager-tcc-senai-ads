import { Router } from 'express'

import webhookProcessing from './intents'

const routes = Router()
routes.post('/chatbot', (request, response) => webhookProcessing(request, response))

export default routes
