import api from './api';

const STORAGE_KEY = 'placetrack_user';

export const authService = {
  // Register a new account (backend hashes the password with BCrypt)
  register: (data) => api.post('/auth/register', data),

  // Verify credentials against registered users in MySQL
  login: (data) => api.post('/auth/login', data),

  // Invalidate the DB-backed session token on the backend, then clear local session
  logout: async () => {
    try {
      await api.post('/auth/logout');
    } catch {
      // best-effort: even if the backend is down we still clear local session
    }
    localStorage.removeItem(STORAGE_KEY);
  },

  // Persist the logged-in user locally after successful login/registration
  saveSession: (user) => localStorage.setItem(STORAGE_KEY, JSON.stringify(user)),

  // Restore the logged-in user (or null)
  getCurrentUser: () => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  // Clear the session (logout)
  logoutLocal: () => localStorage.removeItem(STORAGE_KEY),
};
