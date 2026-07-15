import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to attach JWT token if it exists in localStorage
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default {
  getPatients(params) {
    // params can contain: search, status, priority, page, size, sort
    return api.get('/patients', { params })
  },
  getPatientById(id) {
    return api.get(`/patients/${id}`)
  },
  createPatient(patient) {
    return api.post('/patients', patient)
  },
  updatePatient(id, patient) {
    return api.put(`/patients/${id}`, patient)
  },
  deletePatient(id) {
    return api.delete(`/patients/${id}`)
  },
  getDashboard() {
    return api.get('/dashboard')
  },
  getEpsList() {
    return api.get('/api/eps')
  }
}
