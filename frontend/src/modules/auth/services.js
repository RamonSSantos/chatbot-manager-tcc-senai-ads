import http from '@/services/http-common'

class AuthenticationService {
  authenticate(data) {
    return http.post('/auth', data)
  }

  validateToken() {
    return http.get('/validate-token')
  }

  logout() {
    return http.post('/do-logout')
  }
}

export default new AuthenticationService()
