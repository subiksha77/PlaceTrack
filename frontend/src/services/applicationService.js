import api from './api';

/**
 * Student applications (/api/applications).
 * POST and /my need the X-Auth-Token session header (attached automatically by
 * the shared axios instance) because they resolve the caller's student profile.
 */
export const applicationService = {
  apply: (data) => api.post('/applications', data),
  getMyApplications: () => api.get('/applications/my'),
  getById: (id) => api.get(`/applications/${id}`),
  getAll: (params = {}) => api.get('/applications', { params }),
  updateStatus: (id, status) => api.put(`/applications/${id}/status`, { status }),
  withdraw: (id) => api.put(`/applications/${id}/withdraw`),
};

export default applicationService;
