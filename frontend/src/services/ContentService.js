import http from './http-common'

const path = '/api/content'

class ContentService {
  getAll(currentPage, sortBy, sortDesc) {
    return http.get(
      `${path}?pageNumber=${currentPage}&sortBy=${sortBy}&sortDesc=${sortDesc}`
    )
  }

  getByTitle(title) {
    return http.get(`${path}/search?title=${title}`)
  }

  new(content) {
    return http.post(`${path}/new`, content)
  }

  edit(content) {
    return http.put(`${path}/edit`, content)
  }

  editContentStatus(contentId, status) {
    return http.put(
      `${path}/edit-content-status/id/${contentId}/status/${status}`
    )
  }

  getAllGroupByTopicDescription() {
    return http.get(`${path}/report/get-all-group-by-topic-description`)
  }

  getAllGroupBySectorDescription() {
    return http.get(`${path}/report/get-all-group-by-sector-description`)
  }

  uploadAttachment(file) {
    let formData = new FormData()
    formData.append('file', file)

    return http.post(`${path}/upload-attachment`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  }

  downloadAttachment(filename) {
    return http.get(`${path}/download-attachment/${filename}`, {
      responseType: 'arraybuffer',
    })
  }
}

export default new ContentService()
