import http from './http-common'

const path = '/api/log'

class LogService {
  getByUserIdAndMessageContentId(userId, contentId) {
    return http.get(`${path}/user-id/${userId}/message-content-id/${contentId}`)
  }
}

export default new LogService()
