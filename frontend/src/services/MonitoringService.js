import http from './http-common'

const path = '/api/monitoring'

class LogService {
  getAllGroupByStatus() {
    return http.get(`${path}/report/get-all-group-by-status`)
  }
}

export default new LogService()
