import React, { useState, useEffect, useCallback } from 'react';
import PlacementDriveForm from '../components/PlacementDriveForm';
import ConfirmDialog from '../components/ConfirmDialog';
import DetailModal from '../components/DetailModal';
import { placementDriveService } from '../services/placementDriveService';
import { companyService } from '../services/companyService';
import { jobService } from '../services/jobService';

const DRIVE_STATUSES = ['UPCOMING', 'OPEN', 'CLOSED', 'COMPLETED', 'ONBOARDING'];

function driveStatusBadge(status) {
  const map = {
    UPCOMING: ['badge-info', '📅 Upcoming'],
    OPEN: ['badge-success', '🟢 Open'],
    CLOSED: ['badge-neutral', '⚪ Closed'],
    COMPLETED: ['badge-purple', '🏁 Completed'],
    ONBOARDING: ['badge-warning', '🎒 Onboarding'],
  };
  const [cls, label] = map[status] || ['badge-neutral', status || '—'];
  return <span className={`badge ${cls}`}>{label}</span>;
}

const lpa = (value) => (value == null ? '—' : `₹ ${value} LPA`);

const today = () => new Date().toISOString().slice(0, 10);

function PlacementDrives({ showToast, user }) {
  const [drives, setDrives] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  const [upcomingOnly, setUpcomingOnly] = useState(false);
  const [statusFilter, setStatusFilter] = useState('');
  const [search, setSearch] = useState('');

  const [formOpen, setFormOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [viewData, setViewData] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [saving, setSaving] = useState(false);

  const isStudent = user?.role === 'STUDENT';

  const fetchDrives = useCallback(() => {
    setLoading(true);
    const params = {};
    if (upcomingOnly) params.upcoming = true;
    placementDriveService.getAll(params)
      .then((res) => setDrives(res.data))
      .catch((err) => showToast(err.displayMessage || 'Failed to load placement drives', 'error'))
      .finally(() => setLoading(false));
  }, [upcomingOnly, showToast]);

  useEffect(() => { fetchDrives(); }, [fetchDrives]);

  useEffect(() => {
    companyService.getAll()
      .then((res) => setCompanies(res.data))
      .catch(() => showToast('Failed to load companies', 'error'));
    jobService.getAll()
      .then((res) => setJobs(res.data))
      .catch(() => showToast('Failed to load job opportunities', 'error'));
  }, [showToast]);

  const handleFormSubmit = async (data) => {
    setSaving(true);
    try {
      if (editData) {
        await placementDriveService.update(editData.id, data);
        showToast('Placement drive updated successfully!', 'success');
      } else {
        await placementDriveService.create(data);
        showToast('Placement drive scheduled successfully!', 'success');
      }
      setFormOpen(false);
      fetchDrives();
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to save placement drive', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    try {
      await placementDriveService.delete(deleteTarget.id);
      showToast('Placement drive deleted successfully!', 'success');
      setDeleteTarget(null);
      fetchDrives();
    } catch (err) {
      showToast(err.displayMessage || 'Failed to delete placement drive', 'error');
      setDeleteTarget(null);
    }
  };

  // ===== Derived list (client-side search kept in sync with server filters) =====
  const filtered = drives
    .filter((d) => (statusFilter ? d.status === statusFilter : true))
    .filter((d) => {
      if (!search.trim()) return true;
      const q = search.trim().toLowerCase();
      return (
        (d.companyName || '').toLowerCase().includes(q) ||
        (d.jobRole || '').toLowerCase().includes(q) ||
        (d.workLocation || '').toLowerCase().includes(q)
      );
    })
    .sort((a, b) => (a.driveDate || '').localeCompare(b.driveDate || ''));

  const stats = [
    { key: 'total', label: 'Total Drives', icon: '🚀', value: drives.length, accent: '#6366f1' },
    { key: 'upcoming', label: 'Upcoming Drives', icon: '📅', value: drives.filter((d) => (d.driveDate || '') >= today()).length, accent: '#0ea5e9' },
    { key: 'open', label: 'Open Now', icon: '🟢', value: drives.filter((d) => d.status === 'OPEN').length, accent: '#10b981' },
    { key: 'completed', label: 'Completed', icon: '🏁', value: drives.filter((d) => d.status === 'COMPLETED').length, accent: '#a855f7' },
  ];

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>🚀 Placement Drives</h2>
          <p>Campus recruitment drives with dates, venues and eligibility criteria</p>
        </div>
        {!isStudent && (
          <div className="page-header-right">
            <button className="btn btn-primary" onClick={() => { setEditData(null); setFormOpen(true); }}>
              ＋ Schedule Drive
            </button>
          </div>
        )}
      </div>

      <div className="page-content">
        {/* ===== Stats ===== */}
        <div className="stats-grid">
          {stats.map((s) => (
            <div key={s.key} className="stat-card" style={{ '--card-accent': s.accent }}>
              <div className="stat-icon" style={{ background: `${s.accent}20` }}>{s.icon}</div>
              <div className="stat-content">
                <h3 style={{ color: s.accent }}>{s.value}</h3>
                <p>{s.label}</p>
              </div>
            </div>
          ))}
        </div>

        {/* ===== Toolbar ===== */}
        <div className="toolbar">
          <div className="search-wrapper">
            <span className="search-icon">🔍</span>
            <input
              className="search-input"
              placeholder="Search by company, role or venue..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <select className="filter-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All Status</option>
            {DRIVE_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <label className="checkbox-inline">
            <input type="checkbox" checked={upcomingOnly} onChange={(e) => setUpcomingOnly(e.target.checked)} />
            Upcoming only
          </label>
          {(search || statusFilter || upcomingOnly) && (
            <button className="btn btn-secondary btn-sm" onClick={() => { setSearch(''); setStatusFilter(''); setUpcomingOnly(false); }}>
              ✕ Clear
            </button>
          )}
        </div>

        {/* ===== Drive cards ===== */}
        {loading ? (
          <div className="loading-container"><div className="spinner" /></div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">🚀</div>
            <h3>No Placement Drives Found</h3>
            <p>{search || statusFilter || upcomingOnly
              ? 'No drives match your search/filter criteria.'
              : 'No placement drives scheduled yet.'}</p>
            {!isStudent && (
              <button className="btn btn-primary" onClick={() => { setEditData(null); setFormOpen(true); }}>
                ＋ Schedule the first drive
              </button>
            )}
          </div>
        ) : (
          <div className="drive-grid">
            {filtered.map((d) => (
              <div key={d.id} className="card drive-card">
                <div className="rec-head">
                  <div>
                    <p className="rec-role">{d.jobRole || 'Drive'}</p>
                    <p className="rec-company">🏢 {d.companyName}</p>
                  </div>
                  {driveStatusBadge(d.status)}
                </div>

                <div className="info-grid compact">
                  <div className="info-row"><span className="info-label">📅 Drive Date</span><span className="info-value">{d.driveDate || '—'}</span></div>
                  <div className="info-row"><span className="info-label">⏳ Deadline</span><span className="info-value">{d.applicationDeadline || '—'}</span></div>
                  <div className="info-row"><span className="info-label">📍 Venue</span><span className="info-value">{d.workLocation || '—'}</span></div>
                  <div className="info-row"><span className="info-label">💰 Package</span><span className="info-value">{lpa(d.packageLpa)}</span></div>
                  <div className="info-row"><span className="info-label">🎓 Min CGPA</span><span className="info-value">{d.minCgpa ?? '—'}</span></div>
                  <div className="info-row"><span className="info-label">🏷️ Departments</span><span className="info-value">{d.eligibleDepartments || '—'}</span></div>
                </div>

                <div className="chip-row" style={{ marginTop: 12 }}>
                  {[d.requiredSkills, d.preferredSkills].filter(Boolean).join(',').split(',')
                    .map((s) => s.trim()).filter(Boolean).slice(0, 6)
                    .map((s) => <span key={s} className="chip">{s}</span>)}
                  {!d.requiredSkills && !d.preferredSkills && <span className="chip chip-muted">No skills listed</span>}
                </div>

                <div className="row-actions" style={{ marginTop: 14 }}>
                  <button className="btn btn-secondary btn-sm" onClick={() => setViewData(d)}>👁️ Details</button>
                  {!isStudent && (
                    <>
                      <button className="btn btn-secondary btn-sm" onClick={() => { setEditData(d); setFormOpen(true); }}>✏️ Edit</button>
                      <button className="btn btn-danger btn-sm" onClick={() => setDeleteTarget(d)}>🗑️ Delete</button>
                    </>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* ===== Modals ===== */}
      <PlacementDriveForm
        isOpen={formOpen}
        onClose={() => setFormOpen(false)}
        onSubmit={handleFormSubmit}
        editData={editData}
        companies={companies}
        jobs={jobs}
        loading={saving}
      />

      <DetailModal
        isOpen={!!viewData}
        onClose={() => setViewData(null)}
        icon="🚀"
        title="Placement Drive Details"
        subtitle={viewData ? `${viewData.companyName} — Drive #${viewData.id}` : ''}
        items={viewData ? [
          { label: 'Company', value: viewData.companyName },
          { label: 'Job Role', value: viewData.jobRole },
          { label: 'Package', value: lpa(viewData.packageLpa) },
          { label: 'Drive Date', value: viewData.driveDate },
          { label: 'Application Deadline', value: viewData.applicationDeadline },
          { label: 'Venue / Location', value: viewData.workLocation },
          { label: 'Minimum CGPA', value: viewData.minCgpa },
          { label: 'Eligible Departments', value: viewData.eligibleDepartments },
          { label: 'Eligible Year', value: viewData.eligibleYear },
          { label: 'Graduation Year', value: viewData.graduationYear },
          { label: 'Allowed Backlogs', value: viewData.allowedBacklogs },
          { label: 'Required Skills', value: viewData.requiredSkills },
          { label: 'Preferred Skills', value: viewData.preferredSkills },
          { label: 'Selection Process', value: viewData.selectionProcess },
          { label: 'Status', value: viewData.status, type: 'badge' },
        ] : []}
      />

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Placement Drive"
        message={<>Are you sure you want to delete the drive for <strong>{deleteTarget?.jobRole}</strong> at <strong>{deleteTarget?.companyName}</strong>? This action cannot be undone.</>}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default PlacementDrives;
