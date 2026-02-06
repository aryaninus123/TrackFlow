import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || '/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

export const issueService = {
  getAll: () => api.get('/issues'),
  getById: (id) => api.get(`/issues/${id}`),
  getByStatus: (status) => api.get(`/issues?status=${status}`),
  getMyIssues: () => api.get('/issues/my-issues'),
  getAssignedIssues: () => api.get('/issues/assigned-to-me'),
  create: (data) => api.post('/issues', data),
  update: (id, data) => api.put(`/issues/${id}`, data),
  delete: (id) => api.delete(`/issues/${id}`),
};

export const analyticsService = {
  getAnalytics: () => api.get('/analytics'),
};

export const userService = {
  getAll: () => api.get('/users'),
};

export default api;
