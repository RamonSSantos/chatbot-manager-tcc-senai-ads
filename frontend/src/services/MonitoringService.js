import http from './http-common'

const path = '/api/monitoring'

class LogService {
  getAllGroupByStatus() {
    return http.get(`${path}/report/get-all-group-by-status`)
  }

  getAllMonitoringLogStatusEnum() {
    return http.get(`${path}/report/get-all-monitoring-log-status`)
  }

  getAllGroupBySectorDescription(status, startingDate, endingDate) {
    return http.get(
      `${path}/report/get-all-group-by-sector-description?status=${status}&startingDate=${startingDate}&endingDate=${endingDate}`
    )
  }
}

export default new LogService()
