import http from './http-common'

const path = '/api/topic'

class TopicService {
  getAll(currentPage, sortBy, sortDesc) {
    return http.get(
      `${path}?pageNumber=${currentPage}&sortBy=${sortBy}&sortDesc=${sortDesc}`
    )
  }

  getByDescription(description) {
    return http.get(`${path}/search?description=${description}`)
  }

  new(topic) {
    return http.post(`${path}/new`, topic)
  }

  edit(topic) {
    return http.put(`${path}/edit`, topic)
  }

  getAllActiveTopics() {
    return http.get(`${path}/get-all-active-topics`)
  }
}

export default new TopicService()
