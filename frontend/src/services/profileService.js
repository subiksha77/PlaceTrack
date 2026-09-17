import api from './api';

/**
 * Placement profile of the logged-in account (/api/profile).
 * Every endpoint is scoped to the caller's own profile via the X-Auth-Token
 * header that the shared axios instance attaches automatically.
 * All mutations return the full, freshly recomputed ProfileResponse.
 */
export const profileService = {
  // ===== Profile basics =====
  getProfile: () => api.get('/profile'),
  updateProfile: (data) => api.put('/profile', data),

  // ===== Resume =====
  uploadResume: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/profile/resume', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  downloadResume: () => api.get('/profile/resume', { responseType: 'blob' }),
  deleteResume: () => api.delete('/profile/resume'),

  // ===== Skills =====
  addSkill: (data) => api.post('/profile/skills', data),
  updateSkill: (id, data) => api.put(`/profile/skills/${id}`, data),
  deleteSkill: (id) => api.delete(`/profile/skills/${id}`),

  // ===== Projects =====
  addProject: (data) => api.post('/profile/projects', data),
  updateProject: (id, data) => api.put(`/profile/projects/${id}`, data),
  deleteProject: (id) => api.delete(`/profile/projects/${id}`),

  // ===== Certifications =====
  addCertification: (data) => api.post('/profile/certifications', data),
  updateCertification: (id, data) => api.put(`/profile/certifications/${id}`, data),
  deleteCertification: (id) => api.delete(`/profile/certifications/${id}`),

  // ===== Internships =====
  addInternship: (data) => api.post('/profile/internships', data),
  updateInternship: (id, data) => api.put(`/profile/internships/${id}`, data),
  deleteInternship: (id) => api.delete(`/profile/internships/${id}`),
};

export default profileService;
