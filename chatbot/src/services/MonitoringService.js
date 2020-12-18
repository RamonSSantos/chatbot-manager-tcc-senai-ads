import http from './http-common'

class MonitoringService {
  createLog(id, status) {
    return http.post(`/monitoring/create-log/id/${id}/status/${status}`)
  }
}

export default new MonitoringService()
