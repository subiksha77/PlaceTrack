import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';

function Sidebar({ user, onLogout }) {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    // Clear the DB-backed session token so the backend rejects the user
    await authService.logout();
    onLogout();
    navigate('/login', { replace: true });
  };

  // Initials for the avatar (from the registered name)
  const initials = (user?.fullName || '?')
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join('');

  const roleBadge = user?.role === 'ADMIN'
    ? <span className="role-badge role-admin">Admin</span>
    : <span className="role-badge role-student">Student</span>;

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">🎯</div>
        <div className="sidebar-logo-text">
          <h1>PlaceTrack</h1>
          <p>Placement System</p>
        </div>
      </div>

      <nav className="sidebar-nav">
        <span className="nav-section-label">Main Menu</span>
        {[
          { path: '/', label: 'Dashboard', icon: '📊' },
          { path: '/students', label: 'Students', icon: '🎓' },
          { path: '/companies', label: 'Companies', icon: '🏢' },
          { path: '/placements', label: 'Placements', icon: '📋' },
          { path: '/jobs', label: 'Jobs', icon: '💼' },
          { path: '/drives', label: 'Drives', icon: '🚀' },
          { path: '/profile', label: 'My Profile', icon: '👤' },
        ].map((item) => (
          <button
            key={item.path}
            className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
            onClick={() => navigate(item.path)}
          >
            <span className="nav-item-icon">{item.icon}</span>
            {item.label}
          </button>
        ))}
      </nav>

      {/* Profile area - shows the actual logged-in account from the database */}
      <div className="sidebar-profile">
        <div className="profile-avatar">{initials}</div>
        <div className="profile-info">
          <p className="profile-name">{user?.fullName || '—'}</p>
          <p className="profile-email">{user?.email}</p>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, flexWrap: 'wrap' }}>
            <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>
              {user?.department} • Year {user?.year}
            </span>
            {roleBadge}
          </div>
          {user?.profilePercent != null && (
            <div className="profile-progress" title={`Profile ${user.profilePercent}% complete`}>
              <div className="profile-progress-bar" style={{ width: `${user.profilePercent}%` }} />
            </div>
          )}
        </div>
      </div>

      <div className="sidebar-logout">
        <button className="btn btn-secondary btn-block btn-sm" onClick={handleLogout}>
          🚪 Logout
        </button>
      </div>

      <div className="sidebar-footer">
        <p>PlaceTrack v1.1</p>
      </div>
    </aside>
  );
}

export default Sidebar;

