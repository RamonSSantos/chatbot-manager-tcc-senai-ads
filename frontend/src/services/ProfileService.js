import http from './http-common'

const path = '/api/profile'

class ProfileService {
  getAll() {
    return http.get(`${path}`)
  }

  getAllActiveProfiles() {
    return http.get(`${path}/get-all-active-profiles`)
  }
}

export default new ProfileService()
