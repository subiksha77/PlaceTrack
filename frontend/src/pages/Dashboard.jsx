import React, { useState, useEffect } from 'react';
import { dashboardService } from '../services/dashboardService';

const STAT_CARDS = [
  { key: 'totalStudents', label: 'Total Students', icon: '🎓', accent: '#6366f1' },
  { key: 'totalCompanies', label: 'Total Companies', icon: '🏢', accent: '#0ea5e9' },
  { key: 'totalPlaced', label: 'Students Placed', icon: '✅', accent: '#10b981' },
  { key: 'totalNotPlaced', label: 'Not Yet Placed', icon: '⏳', accent: '#f59e0b' },
  { key: 'totalPlacements', label: 'Placement Records', icon: '📋', accent: '#8b5cf6' },
];

function statusBadge(status) {
  const map = {
    SELECTED: <span className="badge badge-success">● Selected</span>,
    REJECTED: <span className="badge badge-danger">● Rejected</span>,
    PENDING:  <span className="badge badge-warning">● Pending</span>,
  };
  return map[status] || status;
}

function Dashboard({ user }) {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    dashboardService.getStats()
      .then((res) => setStats(res.data))
      .catch(() => setError('Unable to load dashboard. Make sure the backend is running.'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div>
        <div className="page-header">
          <div className="page-header-left">
            <h2>Dashboard</h2>
            <p>Overview of placement activities</p>
          </div>
        </div>
        <div className="page-content">
          <div className="loading-container"><div className="spinner" /></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <div className="page-header">
          <div className="page-header-left"><h2>Dashboard</h2></div>
        </div>
        <div className="page-content">
          <div className="card" style={{ borderColor: '#ef4444', textAlign: 'center', padding: 40 }}>
            <div style={{ fontSize: 48, marginBottom: 16 }}>🔌</div>
            <h3 style={{ color: '#ef4444', marginBottom: 8 }}>Backend Not Connected</h3>
            <p style={{ color: 'var(--text-muted)' }}>{error}</p>
            <p style={{ color: 'var(--text-muted)', marginTop: 8, fontSize: 13 }}>
              Run: <code style={{ color: 'var(--primary-light)' }}>mvn spring-boot:run</code> in the backend directory
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>Welcome, {user?.fullName || 'Guest'} 👋</h2>
          <p>Here's your live placement overview – all numbers come from the database</p>
        </div>
        <div className="page-header-right">
          <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
            🟢 Backend Connected
          </span>
        </div>
      </div>

      <div className="page-content">
        {/* Stats Cards */}
        <div className="stats-grid">
          {STAT_CARDS.map(({ key, label, icon, accent }) => (
            <div key={key} className="stat-card" style={{ '--card-accent': accent }}>
              <div className="stat-icon" style={{ background: `${accent}20` }}>
                {icon}
              </div>
              <div className="stat-content">
                <h3 style={{ color: accent }}>{stats?.[key] ?? 0}</h3>
                <p>{label}</p>
              </div>
            </div>
          ))}
        </div>

        {/* Recent Placements Table */}
        <div className="card">
          <div className="section-header">
            <div>
              <h3>📋 Recent Placements</h3>
              <p>Latest 10 placement records</p>
            </div>
          </div>

          {(!stats?.recentPlacements || stats.recentPlacements.length === 0) ? (
            <div className="empty-state">
              <div className="empty-state-icon">📭</div>
              <h3>No Placements Yet</h3>
              <p>Go to the Placements page and add your first placement record.</p>
            </div>
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Student</th>
                    <th>Company</th>
                    <th>Job Role</th>
                    <th>Package (LPA)</th>
                    <th>Date</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {stats.recentPlacements.map((p, i) => (
                    <tr key={p.id}>
                      <td style={{ color: 'var(--text-muted)', fontWeight: 400 }}>{i + 1}</td>
                      <td>{p.student?.studentName}</td>
                      <td>{p.company?.companyName}</td>
                      <td>{p.jobRole}</td>
                      <td>
                        <span style={{ color: '#10b981', fontWeight: 700 }}>
                          ₹{p.packageLpa} LPA
                        </span>
                      </td>
                      <td style={{ color: 'var(--text-muted)' }}>{p.placementDate}</td>
                      <td>{statusBadge(p.status)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
