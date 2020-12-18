import axios from 'axios'

const http = axios.create({
  baseURL: process.env.WEB_SYSTEM_API_BACKEND,
  withCredentials: true,
})

setTimeout(() => {
  http.defaults.baseURL = process.env.WEB_SYSTEM_API_BACKEND
}, 1)

export default http
