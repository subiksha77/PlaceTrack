import api from './api';

export const placementService = {
  getAll: (params = {}) => api.get('/placements', { params }),
  getById: (id) => api.get(`/placements/${id}`),
  create: (data) => api.post('/placements', data),
  update: (id, data) => api.put(`/placements/${id}`, data),
  delete: (id) => api.delete(`/placements/${id}`),
};
