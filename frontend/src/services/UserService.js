import http from './http-common'

const path = '/api/user'

class UserService {
  getAll(currentPage, sortBy, sortDesc) {
    return http.get(
      `${path}?pageNumber=${currentPage}&sortBy=${sortBy}&sortDesc=${sortDesc}`
    )
  }

  getById(id) {
    return http.get(`${path}/view/${id}`)
  }

  getByFullname(fullname) {
    return http.get(`${path}/search?fullname=${fullname}`)
  }

  new(user) {
    return http.post(`${path}/new`, user)
  }

  edit(user) {
    return http.put(`${path}/edit`, user)
  }

  editUserStatus(userId, status) {
    return http.put(`${path}/edit-user-status/id/${userId}/status/${status}`)
  }

  getOwnUser(userId) {
    return http.get(`${path}/get-own-user/${userId}`)
  }

  editOwnUser(data) {
    return http.put(`${path}/edit-own-user`, data)
  }

  editOwnPassword(data) {
    return http.put(`${path}/edit-own-password`, data)
  }

  getAllGroupByProfileDescription() {
    return http.get(`${path}/report/get-all-group-by-profile-description`)
  }

  getAllGroupBySectorDescription() {
    return http.get(`${path}/report/get-all-group-by-sector-description`)
  }
}

export default new UserService()
