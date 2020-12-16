import http from '@/services/http-common'

class PasswordService {
  forgotPassword(data) {
    return http.post('/forgot-password', data)
  }

  resetPassword(data) {
    return http.put('/reset-password', data)
  }
}

export default new PasswordService()
