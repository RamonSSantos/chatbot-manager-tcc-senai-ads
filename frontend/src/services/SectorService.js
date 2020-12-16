import http from './http-common'

const path = '/api/sector'

class SectorService {
  getAll(currentPage, sortBy, sortDesc) {
    return http.get(
      `${path}?pageNumber=${currentPage}&sortBy=${sortBy}&sortDesc=${sortDesc}`
    )
  }

  getByDescription(description) {
    return http.get(`${path}/search?description=${description}`)
  }

  new(sector) {
    return http.post(`${path}/new`, sector)
  }

  edit(sector) {
    return http.put(`${path}/edit`, sector)
  }

  getAllActiveSectors() {
    return http.get(`${path}/get-all-active-sectors`)
  }
}

export default new SectorService()
