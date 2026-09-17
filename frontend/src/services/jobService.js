import api from './api';

/**
 * Job opportunities (/api/jobs) plus the backend-computed matching helpers
 * exposed by /api/eligibility and /api/recommendations.
 *
 * Eligibility and recommendations are NEVER calculated in the browser - the
 * backend is the single source of truth for both.
 */
export const jobService = {
  // ===== Job opportunities =====
  getAll: (params = {}) => api.get('/jobs', { params }),
  getById: (id) => api.get(`/jobs/${id}`),
  getByCompany: (companyId) => api.get(`/jobs/company/${companyId}`),
  create: (data) => api.post('/jobs', data),
  update: (id, data) => api.put(`/jobs/${id}`, data),
  delete: (id) => api.delete(`/jobs/${id}`),

  // ===== Eligibility (real criteria breakdown from the backend) =====
  checkEligibility: (jobId) => api.get(`/eligibility/check/${jobId}`),

  // ===== Recommendations (match % + missing skills, computed by the backend) =====
  getRecommendations: () => api.get('/recommendations'),
};

export default jobService;
