import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach the DB session token (X-Auth-Token) on every request if the user is logged in.
api.interceptors.request.use((config) => {
  try {
    const user = JSON.parse(localStorage.getItem('placetrack_user') || '');
    if (user?.token) {
      config.headers['X-Auth-Token'] = user.token;
    }
  } catch {
    // ignore malformed session
  }
  return config;
}, (error) => Promise.reject(error));

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        // Session invalid or expired on the backend -> clear frontend session
        try {
          localStorage.removeItem('placetrack_user');
        } catch {}
      }
      const message = error.response.data?.message || error.response.data?.error || 'An error occurred';
      return Promise.reject({ ...error, displayMessage: message });
    } else if (error.request) {
      return Promise.reject({ ...error, displayMessage: 'Cannot connect to server. Make sure the backend is running on port 8080.' });
    }
    return Promise.reject(error);
  }
);

export default api;
