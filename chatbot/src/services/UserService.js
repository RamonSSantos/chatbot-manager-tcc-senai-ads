import http from './http-common'

class UserService {
  createUser(data) {
    return http.post('/user/create-user', data)
  }
}

export default new UserService()
