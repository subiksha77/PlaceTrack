import React, { useState, useEffect, useCallback, useMemo } from 'react';
import CompanyForm from '../components/CompanyForm';
import ConfirmDialog from '../components/ConfirmDialog';
import { companyService } from '../services/companyService';

const PAGE_SIZE = 9;
const TODAY = () => new Date().toISOString().slice(0, 10);

const STATS_CARDS = [
  { key: 'totalCompanies', label: 'Total Companies', icon: '🏢', accent: '#6366f1' },
  { key: 'activeDrives', label: 'Active Drives', icon: '🔔', accent: '#0ea5e9' },
  { key: 'upcomingDrives', label: 'Upcoming (30 days)', icon: '📅', accent: '#f59e0b' },
  { key: 'eligibleOpportunities', label: 'Eligible Opportunities', icon: '🎯', accent: '#10b981' },
];

// Drive status derived from the drive date stored in the database
function driveStatus(company) {
  const today = TODAY();
  if (company.driveDate === today) return { label: 'Live Today', cls: 'badge-warning' };
  if (company.driveDate > today) return { label: 'Upcoming', cls: 'badge-info' };
  return { label: 'Completed', cls: 'badge-neutral' };
}

function formatDriveDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso + 'T00:00:00');
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
}

function Companies({ showToast }) {
  const [companies, setCompanies] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [viewData, setViewData] = useState(null);
  const [saving, setSaving] = useState(false);

  // Search / filter / sort / pagination state
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [cgpaFilter, setCgpaFilter] = useState('');
  const [sortBy, setSortBy] = useState('dateSoon');
  const [page, setPage] = useState(1);

  const fetchData = useCallback(() => {
    setLoading(true);
    Promise.all([companyService.getAll(), companyService.getStats()])
      .then(([cRes, sRes]) => {
        setCompanies(cRes.data);
        setStats(sRes.data);
      })
      .catch(() => showToast('Failed to load company data', 'error'))
      .finally(() => setLoading(false));
  }, [showToast]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleAdd = () => { setEditData(null); setFormOpen(true); };
  const handleEdit = (company) => { setEditData(company); setFormOpen(true); };
  const handleDeleteClick = (company) => setDeleteTarget(company);

  const handleFormSubmit = async (data) => {
    setSaving(true);
    try {
      if (editData) {
        await companyService.update(editData.id, data);
        showToast('Company updated successfully!', 'success');
      } else {
        await companyService.create(data);
        showToast('Company added successfully!', 'success');
      }
      setFormOpen(false);
      fetchData();
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to save company', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    try {
      await companyService.delete(deleteTarget.id);
      showToast('Company deleted successfully!', 'success');
      setDeleteTarget(null);
      fetchData();
    } catch (err) {
      showToast(err.displayMessage || 'Failed to delete company', 'error');
      setDeleteTarget(null);
    }
  };

  // Search -> status filter -> eligibility filter -> sort (applied to live DB data)
  const visible = useMemo(() => {
    let list = [...companies];
    const q = search.trim().toLowerCase();
    if (q) {
      list = list.filter((c) =>
        (c.companyName || '').toLowerCase().includes(q) ||
        (c.jobRole || '').toLowerCase().includes(q) ||
        (c.location || '').toLowerCase().includes(q));
    }
    if (statusFilter) {
      list = list.filter((c) => driveStatus(c).label === statusFilter);
    }
    if (cgpaFilter) {
      list = list.filter((c) => Number(c.eligibilityCgpa) <= Number(cgpaFilter));
    }
    switch (sortBy) {
      case 'packageHigh': list.sort((a, b) => b.packageLpa - a.packageLpa); break;
      case 'packageLow': list.sort((a, b) => a.packageLpa - b.packageLpa); break;
      case 'eligibility': list.sort((a, b) => a.eligibilityCgpa - b.eligibilityCgpa); break;
      case 'name': list.sort((a, b) => (a.companyName || '').localeCompare(b.companyName || '')); break;
      case 'dateSoon':
      default:
        list.sort((a, b) => (a.driveDate || '').localeCompare(b.driveDate || ''));
        break;
    }
    return list;
  }, [companies, search, statusFilter, cgpaFilter, sortBy]);

  const totalPages = Math.max(1, Math.ceil(visible.length / PAGE_SIZE));
  const safePage = Math.min(page, totalPages);
  const pageItems = visible.slice((safePage - 1) * PAGE_SIZE, safePage * PAGE_SIZE);
  const hasFilters = search || statusFilter || cgpaFilter;

  const clearFilters = () => {
    setSearch(''); setStatusFilter(''); setCgpaFilter(''); setSortBy('dateSoon'); setPage(1);
  };

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>🏢 Company Dashboard</h2>
          <p>Explore recruiting companies and their placement drives</p>
        </div>
        <div className="page-header-right">
          <button className="btn btn-primary" onClick={handleAdd}>＋ Add Company</button>
        </div>
      </div>

      <div className="page-content">
        {/* Summary cards - computed from the database */}
        <div className="stats-grid">
          {STATS_CARDS.map(({ key, label, icon, accent }) => (
            <div key={key} className="stat-card" style={{ '--card-accent': accent }}>
              <div className="stat-icon" style={{ background: `${accent}20` }}>{icon}</div>
              <div className="stat-content">
                <h3 style={{ color: accent }}>{stats?.[key] ?? 0}</h3>
                <p>{label}</p>
              </div>
            </div>
          ))}
        </div>

        {/* Search + Filters + Sort toolbar */}
        <div className="toolbar filter-bar">
          <div className="search-wrapper">
            <span className="search-icon">🔍</span>
            <input className="search-input" placeholder="Search by name, role or location..."
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(1); }} />
          </div>
          <select className="filter-select" value={statusFilter}
            onChange={(e) => { setStatusFilter(e.target.value); setPage(1); }}>
            <option value="">All drive statuses</option>
            <option value="Upcoming">Upcoming drives</option>
            <option value="Live Today">Live today</option>
            <option value="Completed">Completed</option>
          </select>
          <select className="filter-select" value={cgpaFilter}
            onChange={(e) => { setCgpaFilter(e.target.value); setPage(1); }}>
            <option value="">Any eligibility CGPA</option>
            <option value="6">Eligible up to 6.0 CGPA</option>
            <option value="6.5">Eligible up to 6.5 CGPA</option>
            <option value="7">Eligible up to 7.0 CGPA</option>
            <option value="7.5">Eligible up to 7.5 CGPA</option>
            <option value="8">Eligible up to 8.0 CGPA</option>
            <option value="10">Eligible up to 10.0 CGPA</option>
          </select>
          <select className="filter-select" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
            <option value="dateSoon">Sort: Drive date (soonest)</option>
            <option value="packageHigh">Sort: Package (high → low)</option>
            <option value="packageLow">Sort: Package (low → high)</option>
            <option value="eligibility">Sort: Min CGPA (low → high)</option>
            <option value="name">Sort: Company (A → Z)</option>
          </select>
          {hasFilters && (
            <button className="btn btn-secondary btn-sm" onClick={clearFilters}>✕ Clear</button>
          )}
        </div>

        {/* Company cards */}
        {loading ? (
          <div className="loading-container"><div className="spinner" /></div>
        ) : companies.length === 0 ? (
          <div className="card">
            <div className="empty-state">
              <div className="empty-state-icon">🏢</div>
              <h3>No Companies Yet</h3>
              <p>Add your first recruiting company to see it here.</p>
              <button className="btn btn-primary" onClick={handleAdd}>＋ Add Company</button>
            </div>
          </div>
        ) : visible.length === 0 ? (
          <div className="card">
            <div className="empty-state">
              <div className="empty-state-icon">🔍</div>
              <h3>No Matches</h3>
              <p>No companies match the current search or filters.</p>
              <button className="btn btn-secondary" onClick={clearFilters}>✕ Clear filters</button>
            </div>
          </div>
        ) : (
          <>
            <div className="result-count">
              Showing <strong>{(safePage - 1) * PAGE_SIZE + 1}–{Math.min(safePage * PAGE_SIZE, visible.length)}</strong> of{' '}
              <strong>{visible.length}</strong> companies
            </div>

            <div className="company-grid">
              {pageItems.map((c) => {
                const st = driveStatus(c);
                return (
                  <div key={c.id} className="company-card">
                    <div className="company-card-header">
                      <div className="company-logo">{(c.companyName || '?')[0]}</div>
                      <div className="company-card-title">
                        <h3>{c.companyName}</h3>
                        <p>{c.jobRole}</p>
                      </div>
                      <span className={`badge ${st.cls}`}>{st.label}</span>
                    </div>

                    <div className="company-package">₹{c.packageLpa} LPA</div>

                    <div className="company-meta">
                      <span>🎓 Eligibility: <strong>{c.eligibilityCgpa} CGPA</strong></span>
                      <span>📍 {c.location}</span>
                      <span>📅 {formatDriveDate(c.driveDate)}</span>
                    </div>

                    <div className="company-actions">
                      <button className="btn btn-primary btn-sm" onClick={() => setViewData(c)}>View Details</button>
                      <button className="btn btn-secondary btn-sm" onClick={() => handleEdit(c)}>✏️ Edit</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDeleteClick(c)}>🗑️</button>
                    </div>
                  </div>
                );
              })}
            </div>

            {totalPages > 1 && (
              <div className="pagination">
                <button className="page-btn" disabled={safePage === 1}
                  onClick={() => setPage(safePage - 1)}>‹ Prev</button>
                {Array.from({ length: totalPages }, (_, i) => i + 1).map((n) => (
                  <button key={n} className={`page-btn ${n === safePage ? 'active' : ''}`}
                    onClick={() => setPage(n)}>{n}</button>
                ))}
                <button className="page-btn" disabled={safePage === totalPages}
                  onClick={() => setPage(safePage + 1)}>Next ›</button>
              </div>
            )}
          </>
        )}
      </div>

      <CompanyForm isOpen={formOpen} onClose={() => setFormOpen(false)}
        onSubmit={handleFormSubmit} editData={editData} loading={saving} />

      {/* Professional company details view with actions */}
      {viewData && (
        <div className="modal-overlay" onClick={() => setViewData(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>🏢 {viewData.companyName}</h3>
              <button className="modal-close" onClick={() => setViewData(null)}>×</button>
            </div>
            <div className="modal-body">
              <div className="detail-hero">
                <div className="company-logo company-logo-lg">{(viewData.companyName || '?')[0]}</div>
                <div>
                  <h4 style={{ fontSize: 18 }}>{viewData.jobRole}</h4>
                  <span className={`badge ${driveStatus(viewData).cls}`}>{driveStatus(viewData).label}</span>
                </div>
                <div className="detail-hero-package">₹{viewData.packageLpa} LPA</div>
              </div>

              <div className="detail-list">
                <div className="detail-row">
                  <span className="detail-label">Company Name</span>
                  <span className="detail-value">{viewData.companyName}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Job Role</span>
                  <span className="detail-value">{viewData.jobRole}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Package (LPA)</span>
                  <span className="detail-value" style={{ color: 'var(--success)', fontWeight: 700 }}>₹{viewData.packageLpa} LPA</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Eligibility CGPA</span>
                  <span className="detail-value">{viewData.eligibilityCgpa}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Drive Date</span>
                  <span className="detail-value">📅 {formatDriveDate(viewData.driveDate)}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Location</span>
                  <span className="detail-value">📍 {viewData.location}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Drive Status</span>
                  <span className="detail-value">{driveStatus(viewData).label}</span>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setViewData(null)}>Close</button>
              <button className="btn btn-danger" onClick={() => { setViewData(null); handleDeleteClick(viewData); }}>🗑️ Delete</button>
              <button className="btn btn-primary" onClick={() => { setViewData(null); handleEdit(viewData); }}>✏️ Edit</button>
            </div>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Company"
        message={<>Are you sure you want to delete <strong>{deleteTarget?.companyName}</strong>? This may affect related placements.</>}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default Companies;
