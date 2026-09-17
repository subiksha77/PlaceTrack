import React, { useState, useCallback } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Toast from './components/Toast';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Companies from './pages/Companies';
import Placements from './pages/Placements';
import Profile from './pages/Profile';
import Jobs from './pages/Jobs';
import PlacementDrives from './pages/PlacementDrives';
import { authService } from './services/authService';

function App() {
  // Restore the logged-in user (if any) from the local session
  const [user, setUser] = useState(() => authService.getCurrentUser());
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((message, type = 'info') => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const handleAuthSuccess = useCallback((u) => setUser(u), []);

  const handleLogout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  return (
    <Router>
      <Routes>
        {/* Public: only visible when logged out */}
        <Route path="/login" element={user ? <Navigate to="/" replace /> : <Login onLogin={handleAuthSuccess} />} />
        <Route path="/register" element={user ? <Navigate to="/" replace /> : <Register onRegister={handleAuthSuccess} />} />

        {/* Protected app shell */}
        <Route
          path="*"
          element={
            user ? (
              <div className="app-layout">
                <Sidebar user={user} onLogout={handleLogout} />
                <div className="main-content">
                  <Routes>
                    <Route path="/" element={<Dashboard user={user} />} />
                    <Route path="/students" element={<Students showToast={showToast} />} />
                    <Route path="/companies" element={<Companies showToast={showToast} />} />
                    <Route path="/placements" element={<Placements showToast={showToast} />} />
                    <Route path="/jobs" element={<Jobs showToast={showToast} user={user} />} />
                    <Route path="/drives" element={<PlacementDrives showToast={showToast} user={user} />} />
                    <Route path="/profile" element={<Profile showToast={showToast} user={user} />} />
                  </Routes>
                </div>
              </div>
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />
      </Routes>
      <Toast toasts={toasts} removeToast={removeToast} />
    </Router>
  );
}

export default App;

