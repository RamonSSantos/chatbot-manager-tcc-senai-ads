import http from './http-common'

const path = '/api/monitoring'

class EventService {
  getAll(currentPage, sortBy, sortDesc) {
    return http.get(
      `${path}?pageNumber=${currentPage}&sortBy=${sortBy}&sortDesc=${sortDesc}`
    )
  }

  getByContentTitle(title) {
    return http.get(`${path}/search?title=${title}`)
  }

  editStatus(id, status) {
    return http.put(`${path}/edit-status/id/${id}/status/${status}`)
  }
}

export default new EventService()
