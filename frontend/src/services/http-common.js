import axios from 'axios'
import store from '@/store'
import routes from '@/router'

const http = axios.create({
  baseURL: process.env.VUE_APP_API_BACKEND,
  withCredentials: true,
})

http.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (response) => {
    if (
      response.config.method === 'get' ||
      response.config.url === '/auth' ||
      response.config.url === '/api/content/upload-attachment'
    ) {
      return response
    }

    if (response.config.url === '/forgot-password') {
      return response
    } else {
      const notification = {
        type: 'green darken-2',
        message: response.data,
        icon: 'mdi-checkbox-marked-circle',
      }
      store.dispatch('notification/add', notification, { root: true })
    }

    return response
  },
  (error) => {
    if (error.response.data.message) {
      const notification = {
        type: 'red darken-2',
        message: error.response.data.message,
        icon: 'mdi-alert-circle',
      }
      store.dispatch('notification/add', notification, { root: true })
    }

    if (error.response.status === 500) {
      routes.push({ name: 'NetworkIssue' })
    }

    if (error.response.status === 403) {
      const notification = {
        type: 'red darken-2',
        message: 'Autenticação inválida ou acesso negado.',
        icon: 'mdi-alert-circle',
      }
      store.dispatch('notification/add', notification, { root: true })
    }
    return Promise.reject(error)
  }
)

export default http
