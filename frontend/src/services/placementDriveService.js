import api from './api';

/**
 * Placement drives on the company/college calendar (/api/placement-drives).
 * The backend also exposes the same routes under the /api/drives alias.
 */
export const placementDriveService = {
  getAll: (params = {}) => api.get('/placement-drives', { params }),
  getById: (id) => api.get(`/placement-drives/${id}`),
  getByCompany: (companyId) => api.get(`/placement-drives/by-company/${companyId}`),
  create: (data) => api.post('/placement-drives', data),
  update: (id, data) => api.put(`/placement-drives/${id}`, data),
  delete: (id) => api.delete(`/placement-drives/${id}`),
};

export default placementDriveService;
