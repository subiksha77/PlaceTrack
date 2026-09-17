import React, { useState, useEffect, useCallback } from 'react';
import JobForm from '../components/JobForm';
import ConfirmDialog from '../components/ConfirmDialog';
import DetailModal from '../components/DetailModal';
import EligibilityModal from '../components/EligibilityModal';
import { jobService } from '../services/jobService';
import { companyService } from '../services/companyService';
import { applicationService } from '../services/applicationService';

const JOB_STATUSES = ['OPEN', 'CLOSED', 'CANCELLED', 'COMPLETED'];

const JOB_TYPE_LABELS = {
  FULL_TIME: 'Full Time', INTERNSHIP: 'Internship', CONTRACT: 'Contract',
  CONTRACT_INTERNSHIP: 'Contract + Internship', WFH: 'Work From Home', OTHER: 'Other',
};

function jobStatusBadge(status) {
  const map = {
    OPEN: ['badge-success', '🟢 Open'],
    CLOSED: ['badge-neutral', '⚪ Closed'],
    CANCELLED: ['badge-danger', '⛔ Cancelled'],
    COMPLETED: ['badge-info', '🏁 Completed'],
  };
  const [cls, label] = map[status] || ['badge-neutral', status || '—'];
  return <span className={`badge ${cls}`}>{label}</span>;
}

function applicationStatusBadge(status) {
  const map = {
    APPLIED: ['badge-info', '📨 Applied'],
    SHORTLISTED: ['badge-purple', '⭐ Shortlisted'],
    ASSESSMENT: ['badge-warning', '📝 Assessment'],
    INTERVIEW: ['badge-warning', '🎤 Interview'],
    SELECTED: ['badge-success', '🎉 Selected'],
    REJECTED: ['badge-danger', '❌ Rejected'],
    WITHDRAWN: ['badge-neutral', '↩️ Withdrawn'],
  };
  const [cls, label] = map[status] || ['badge-neutral', status || '—'];
  return <span className={`badge ${cls}`}>{label}</span>;
}

const lpa = (value) => (value == null ? '—' : `₹ ${value} LPA`);

/** Percentage shown as a coloured match bar. */
function MatchBar({ percent }) {
  const colour = percent >= 75 ? '#10b981' : percent >= 50 ? '#f59e0b' : '#ef4444';
  return (
    <div className="match-bar">
      <div className="progress-track">
        <div className="progress-fill" style={{ width: `${percent}%`, background: colour }} />
      </div>
      <span className="match-value" style={{ color: colour }}>{percent}%</span>
    </div>
  );
}

function Jobs({ showToast, user }) {
  const [jobs, setJobs] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [myApplications, setMyApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [recLoading, setRecLoading] = useState(false);
  const [appsLoading, setAppsLoading] = useState(false);

  const [tab, setTab] = useState('jobs');
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const [formOpen, setFormOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [viewData, setViewData] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [applyTarget, setApplyTarget] = useState(null);
  const [applyNotes, setApplyNotes] = useState('');
  const [saving, setSaving] = useState(false);

  const [eligibility, setEligibility] = useState({ open: false, loading: false, result: null, jobRole: '' });

  const isStudent = user?.role === 'STUDENT';

  const fetchJobs = useCallback(() => {
    setLoading(true);
    const params = {};
    if (search) params.search = search;
    if (statusFilter) params.status = statusFilter;
    jobService.getAll(params)
      .then((res) => setJobs(res.data))
      .catch((err) => showToast(err.displayMessage || 'Failed to load job opportunities', 'error'))
      .finally(() => setLoading(false));
  }, [search, statusFilter, showToast]);

  useEffect(() => {
    const timer = setTimeout(fetchJobs, 300);
    return () => clearTimeout(timer);
  }, [fetchJobs]);

  useEffect(() => {
    companyService.getAll()
      .then((res) => setCompanies(res.data))
      .catch(() => showToast('Failed to load companies', 'error'));
  }, [showToast]);

  const fetchRecommendations = useCallback(() => {
    if (!isStudent) return;
    setRecLoading(true);
    jobService.getRecommendations()
      .then((res) => setRecommendations(res.data))
      .catch((err) => showToast(err.displayMessage || 'Failed to load recommendations', 'error'))
      .finally(() => setRecLoading(false));
  }, [isStudent, showToast]);

  const fetchMyApplications = useCallback(() => {
    if (!isStudent) return;
    setAppsLoading(true);
    applicationService.getMyApplications()
      .then((res) => setMyApplications(res.data))
      .catch((err) => showToast(err.displayMessage || 'Failed to load your applications', 'error'))
      .finally(() => setAppsLoading(false));
  }, [isStudent, showToast]);

  useEffect(() => { fetchRecommendations(); }, [fetchRecommendations]);
  useEffect(() => { fetchMyApplications(); }, [fetchMyApplications]);

  // ===== Create / update / delete =====
  const handleFormSubmit = async (data) => {
    setSaving(true);
    try {
      if (editData) {
        await jobService.update(editData.id, data);
        showToast('Job opportunity updated successfully!', 'success');
      } else {
        await jobService.create(data);
        showToast('Job opportunity added successfully!', 'success');
      }
      setFormOpen(false);
      fetchJobs();
      fetchRecommendations();
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to save job opportunity', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    try {
      await jobService.delete(deleteTarget.id);
      showToast('Job opportunity deleted successfully!', 'success');
      setDeleteTarget(null);
      fetchJobs();
      fetchRecommendations();
    } catch (err) {
      showToast(err.displayMessage || 'Failed to delete job opportunity', 'error');
      setDeleteTarget(null);
    }
  };

  // ===== Eligibility (always computed by the backend) =====
  const handleCheckEligibility = async (job) => {
    setEligibility({ open: true, loading: true, result: null, jobRole: job.jobRole });
    try {
      const res = await jobService.checkEligibility(job.id);
      setEligibility({ open: true, loading: false, result: res.data, jobRole: job.jobRole });
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to check eligibility', 'error');
      setEligibility({ open: false, loading: false, result: null, jobRole: '' });
    }
  };

  // ===== Apply / withdraw =====
  const handleApplySubmit = async (e) => {
    e.preventDefault();
    if (!applyTarget) return;
    setSaving(true);
    try {
      await applicationService.apply({ jobOpportunityId: applyTarget.id, notes: applyNotes.trim() || null });
      showToast('Application submitted successfully!', 'success');
      setApplyTarget(null);
      setApplyNotes('');
      fetchMyApplications();
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to submit application', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleWithdraw = async (application) => {
    try {
      await applicationService.withdraw(application.id);
      showToast('Application withdrawn.', 'success');
      fetchMyApplications();
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to withdraw application', 'error');
    }
  };

  const appliedJobIds = new Set(myApplications.map((a) => a.jobOpportunityId));
  const eligibleCount = recommendations.filter((r) => r.eligible).length;

  const stats = [
    { key: 'total', label: 'Total Opportunities', icon: '💼', value: jobs.length, accent: '#6366f1' },
    { key: 'open', label: 'Open Now', icon: '🟢', value: jobs.filter((j) => j.status === 'OPEN').length, accent: '#10b981' },
    { key: 'eligible', label: 'You Are Eligible For', icon: '🎯', value: isStudent ? eligibleCount : '—', accent: '#f59e0b' },
    { key: 'applied', label: 'My Applications', icon: '📨', value: isStudent ? myApplications.length : '—', accent: '#0ea5e9' },
  ];

  const tabs = [
    { key: 'jobs', label: '💼 All Opportunities', count: jobs.length },
  ];
  if (isStudent) {
    tabs.push({ key: 'recommended', label: '⭐ Recommended For Me', count: recommendations.length });
    tabs.push({ key: 'applications', label: '📨 My Applications', count: myApplications.length });
  }

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>💼 Job Opportunities</h2>
          <p>Openings published by companies, with server-computed eligibility and matches</p>
        </div>
        {!isStudent && (
          <div className="page-header-right">
            <button className="btn btn-primary" onClick={() => { setEditData(null); setFormOpen(true); }}>
              ＋ Add Opportunity
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

        {/* ===== Tabs ===== */}
        <div className="tabs">
          {tabs.map((t) => (
            <button
              key={t.key}
              className={`tab ${tab === t.key ? 'active' : ''}`}
              onClick={() => setTab(t.key)}
            >
              {t.label} <span className="tab-count">{t.count}</span>
            </button>
          ))}
        </div>

        {tab === 'jobs' && (
          <>
            {/* Toolbar */}
            <div className="toolbar">
              <div className="search-wrapper">
                <span className="search-icon">🔍</span>
                <input
                  className="search-input"
                  placeholder="Search by role, company or location..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
              <select className="filter-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                <option value="">All Status</option>
                {JOB_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
              {(search || statusFilter) && (
                <button className="btn btn-secondary btn-sm" onClick={() => { setSearch(''); setStatusFilter(''); }}>
                  ✕ Clear
                </button>
              )}
            </div>

            {/* Table */}
            <div className="card" style={{ padding: 0 }}>
              {loading ? (
                <div className="loading-container"><div className="spinner" /></div>
              ) : jobs.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-state-icon">💼</div>
                  <h3>No Job Opportunities Found</h3>
                  <p>{search || statusFilter
                    ? 'No opportunities match your search/filter criteria.'
                    : 'No job opportunities published yet.'}</p>
                </div>
              ) : (
                <>
                  <div className="card-toolbar">
                    <span className="count-text">
                      Showing <strong>{jobs.length}</strong> opportunity(ies)
                    </span>
                  </div>
                  <div className="table-container" style={{ borderRadius: 0, border: 'none' }}>
                    <table>
                      <thead>
                        <tr>
                          <th>ID</th><th>Company</th><th>Role</th><th>Package</th><th>Type</th>
                          <th>Location</th><th>Min CGPA</th><th>Deadline</th><th>Status</th><th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {jobs.map((job) => {
                          const applied = appliedJobIds.has(job.id);
                          return (
                            <tr key={job.id}>
                              <td style={{ color: 'var(--text-muted)', fontWeight: 400 }}>#{job.id}</td>
                              <td><strong>{job.companyName}</strong></td>
                              <td>{job.jobRole}</td>
                              <td><span className="package-text">{lpa(job.packageLpa)}</span></td>
                              <td><span className="badge badge-neutral">{JOB_TYPE_LABELS[job.jobType] || job.jobType || '—'}</span></td>
                              <td style={{ color: 'var(--text-muted)' }}>{job.workLocation || '—'}</td>
                              <td>{job.minCgpa ?? '—'}</td>
                              <td style={{ color: 'var(--text-muted)' }}>{job.applicationDeadline || '—'}</td>
                              <td>{jobStatusBadge(job.status)}</td>
                              <td>
                                <div className="row-actions">
                                  <button className="btn btn-secondary btn-sm" title="View details" onClick={() => setViewData(job)}>👁️</button>
                                  {isStudent && (
                                    <button className="btn btn-secondary btn-sm" title="Check eligibility" onClick={() => handleCheckEligibility(job)}>🎯</button>
                                  )}
                                  {isStudent && (
                                    applied ? (
                                      <span className="badge badge-success" title="Already applied">✔ Applied</span>
                                    ) : (
                                      <button
                                        className="btn btn-primary btn-sm"
                                        title="Apply"
                                        onClick={() => { setApplyTarget(job); setApplyNotes(''); }}
                                      >
                                        📨 Apply
                                      </button>
                                    )
                                  )}
                                  {!isStudent && (
                                    <>
                                      <button className="btn btn-secondary btn-sm" title="Edit" onClick={() => { setEditData(job); setFormOpen(true); }}>✏️</button>
                                      <button className="btn btn-danger btn-sm" title="Delete" onClick={() => setDeleteTarget(job)}>🗑️</button>
                                    </>
                                  )}
                                </div>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                </>
              )}
            </div>
          </>
        )}

        {/* ===== Recommended (server-computed match %) ===== */}
        {tab === 'recommended' && (
          recLoading ? (
            <div className="loading-container"><div className="spinner" /></div>
          ) : recommendations.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">⭐</div>
              <h3>No Recommendations Yet</h3>
              <p>Add skills and complete your profile so the server can match you with open opportunities.</p>
            </div>
          ) : (
            <div className="rec-grid">
              {recommendations.map((r) => (
                <div key={r.jobId} className={`card rec-card ${r.eligible ? 'eligible' : 'not-eligible'}`}>
                  <div className="rec-head">
                    <div>
                      <p className="rec-role">{r.jobRole}</p>
                      <p className="rec-company">🏢 {r.companyName}{r.location ? ` • ${r.location}` : ''}</p>
                    </div>
                    <span className={`badge ${r.eligible ? 'badge-success' : 'badge-danger'}`}>
                      {r.eligible ? '✅ Eligible' : '🚫 Not eligible'}
                    </span>
                  </div>

                  <span className="package-text">{lpa(r.packageLpa)}</span>
                  <MatchBar percent={r.matchPercentage} />

                  {r.reason && <p className="rec-reason">💡 {r.reason}</p>}

                  <div className="rec-skills">
                    <div>
                      <p className="form-section-title">✔ Matching skills</p>
                      <div className="chip-row">
                        {(r.matchingSkills || []).length === 0
                          ? <span className="chip chip-muted">None</span>
                          : r.matchingSkills.map((s) => <span key={s} className="chip chip-success">{s}</span>)}
                      </div>
                    </div>
                    <div>
                      <p className="form-section-title">✖ Missing skills</p>
                      <div className="chip-row">
                        {(r.missingSkills || []).length === 0
                          ? <span className="chip chip-muted">None</span>
                          : r.missingSkills.map((s) => <span key={s} className="chip chip-danger">{s}</span>)}
                      </div>
                    </div>
                  </div>

                  <div className="row-actions" style={{ marginTop: 14 }}>
                    <button className="btn btn-secondary btn-sm"
                      onClick={() => handleCheckEligibility({ id: r.jobId, jobRole: r.jobRole })}>
                      🎯 Full eligibility report
                    </button>
                    {appliedJobIds.has(r.jobId) ? (
                      <span className="badge badge-success">✔ Applied</span>
                    ) : (
                      <button className="btn btn-primary btn-sm"
                        onClick={() => { setApplyTarget({ id: r.jobId, jobRole: r.jobRole, companyName: r.companyName }); setApplyNotes(''); }}>
                        📨 Apply
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )
        )}

        {/* ===== My applications ===== */}
        {tab === 'applications' && (
          <div className="card" style={{ padding: 0 }}>
            {appsLoading ? (
              <div className="loading-container"><div className="spinner" /></div>
            ) : myApplications.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">📨</div>
                <h3>No Applications Yet</h3>
                <p>Apply to an opportunity from the list and track its status here.</p>
              </div>
            ) : (
              <div className="table-container" style={{ borderRadius: 0, border: 'none' }}>
                <table>
                  <thead>
                    <tr>
                      <th>ID</th><th>Company</th><th>Role</th><th>Package</th>
                      <th>Applied On</th><th>Status</th><th>Notes</th><th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myApplications.map((a) => (
                      <tr key={a.id}>
                        <td style={{ color: 'var(--text-muted)', fontWeight: 400 }}>#{a.id}</td>
                        <td><strong>{a.companyName}</strong></td>
                        <td>{a.jobRole}</td>
                        <td><span className="package-text">{lpa(a.packageLpa)}</span></td>
                        <td style={{ color: 'var(--text-muted)' }}>{a.appliedDate || '—'}</td>
                        <td>{applicationStatusBadge(a.status)}</td>
                        <td style={{ color: 'var(--text-muted)' }}>{a.notes || '—'}</td>
                        <td>
                          <div className="row-actions">
                            {a.status !== 'WITHDRAWN' && a.status !== 'SELECTED' && a.status !== 'REJECTED' ? (
                              <button className="btn btn-danger btn-sm" onClick={() => handleWithdraw(a)}>↩️ Withdraw</button>
                            ) : (
                              <span style={{ color: 'var(--text-muted)', fontSize: 13 }}>—</span>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>

      {/* ===== Modals ===== */}
      <JobForm
        isOpen={formOpen}
        onClose={() => setFormOpen(false)}
        onSubmit={handleFormSubmit}
        editData={editData}
        companies={companies}
        loading={saving}
      />

      <DetailModal
        isOpen={!!viewData}
        onClose={() => setViewData(null)}
        icon="💼"
        title="Job Opportunity Details"
        subtitle={viewData ? `${viewData.companyName} — Opportunity #${viewData.id}` : ''}
        items={viewData ? [
          { label: 'Company', value: viewData.companyName },
          { label: 'Job Role', value: viewData.jobRole },
          { label: 'Package', value: lpa(viewData.packageLpa) },
          { label: 'Job Type', value: JOB_TYPE_LABELS[viewData.jobType] || viewData.jobType },
          { label: 'Work Location', value: viewData.workLocation },
          { label: 'Minimum CGPA', value: viewData.minCgpa },
          { label: 'Eligible Departments', value: viewData.eligibleDepartments },
          { label: 'Eligible Year', value: viewData.eligibleYear },
          { label: 'Graduation Year', value: viewData.graduationYear },
          { label: 'Allowed Backlogs', value: viewData.allowedBacklogs },
          { label: 'Required Skills', value: viewData.requiredSkills },
          { label: 'Preferred Skills', value: viewData.preferredSkills },
          { label: 'Drive Date', value: viewData.driveDate },
          { label: 'Application Deadline', value: viewData.applicationDeadline },
          { label: 'Selection Process', value: viewData.selectionProcess },
          { label: 'Description', value: viewData.jobDescription },
          { label: 'Status', value: viewData.status, type: 'badge' },
          { label: 'Linked Drives', value: (viewData.drives || []).length },
        ] : []}
      />

      <EligibilityModal
        isOpen={eligibility.open}
        onClose={() => setEligibility({ open: false, loading: false, result: null, jobRole: '' })}
        result={eligibility.result}
        loading={eligibility.loading}
        jobRole={eligibility.jobRole}
      />

      {/* Apply confirmation with optional note */}
      {applyTarget && (
        <div className="modal-overlay" onClick={() => setApplyTarget(null)}>
          <div className="modal modal-sm" onClick={(e) => e.stopPropagation()}>
            <form onSubmit={handleApplySubmit}>
              <div className="modal-header">
                <h3>📨 Apply</h3>
                <button type="button" className="modal-close" onClick={() => setApplyTarget(null)}>×</button>
              </div>
              <div className="modal-body">
                <p className="confirm-text" style={{ marginBottom: 16 }}>
                  Applying to <strong>{applyTarget.jobRole}</strong>
                  {applyTarget.companyName ? <> at <strong>{applyTarget.companyName}</strong></> : null}.
                  Your uploaded resume is attached automatically.
                </p>
                <div className="form-group">
                  <label className="form-label">Note to recruiter (optional)</label>
                  <textarea
                    className="form-input"
                    rows={3}
                    maxLength={500}
                    placeholder="Why are you a good fit?"
                    value={applyNotes}
                    onChange={(e) => setApplyNotes(e.target.value)}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setApplyTarget(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Submitting...' : '📨 Submit Application'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Job Opportunity"
        message={<>Are you sure you want to delete <strong>{deleteTarget?.jobRole}</strong> at <strong>{deleteTarget?.companyName}</strong>? This action cannot be undone.</>}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default Jobs;
