import http from './http-common'

class MessageService {
  createLog(id, status, userId) {
    return http.post(`/message/create-log/id/${id}/status/${status}/user-id/${userId}`)
  }
}

export default new MessageService()
