import { Payload } from 'dialogflow-fulfillment'

export default function createPayload(json) {
  const payload = new Payload(
    'hangouts',
    json,
    { rawPayload: true, sendAsMessage: true },
  )

  return payload
}
