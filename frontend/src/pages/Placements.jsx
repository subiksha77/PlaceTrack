import React, { useState, useEffect, useCallback } from 'react';
import PlacementForm from '../components/PlacementForm';
import ConfirmDialog from '../components/ConfirmDialog';
import DetailModal from '../components/DetailModal';
import { placementService } from '../services/placementService';
import { studentService } from '../services/studentService';
import { companyService } from '../services/companyService';

function statusBadge(status) {
  const map = {
    SELECTED: <span className="badge badge-success">✅ Selected</span>,
    REJECTED: <span className="badge badge-danger">❌ Rejected</span>,
    PENDING:  <span className="badge badge-warning">⏳ Pending</span>,
  };
  return map[status] || <span className="badge">{status}</span>;
}

function Placements({ showToast }) {
  const [placements, setPlacements] = useState([]);
  const [students, setStudents] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [viewData, setViewData] = useState(null);
  const [saving, setSaving] = useState(false);
  const [statusFilter, setStatusFilter] = useState('');
  const [companyFilter, setCompanyFilter] = useState('');

  const fetchData = useCallback(() => {
    setLoading(true);
    const params = {};
    if (statusFilter) params.status = statusFilter;
    if (companyFilter) params.companyId = companyFilter;

    Promise.all([
      placementService.getAll(params),
      studentService.getAll(),
      companyService.getAll(),
    ])
      .then(([pRes, sRes, cRes]) => {
        setPlacements(pRes.data);
        setStudents(sRes.data);
        setCompanies(cRes.data);
      })
      .catch(() => showToast('Failed to load placement data', 'error'))
      .finally(() => setLoading(false));
  }, [statusFilter, companyFilter, showToast]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleAdd = () => { setEditData(null); setFormOpen(true); };
  const handleEdit = (p) => { setEditData(p); setFormOpen(true); };
  const handleView = (p) => setViewData(p);
  const handleDeleteClick = (p) => setDeleteTarget(p);

  const handleFormSubmit = async (data) => {
    setSaving(true);
    try {
      if (editData) {
        await placementService.update(editData.id, data);
        showToast('Placement updated successfully!', 'success');
      } else {
        await placementService.create(data);
        showToast('Placement added successfully!', 'success');
      }
      setFormOpen(false);
      fetchData();
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to save placement', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    try {
      await placementService.delete(deleteTarget.id);
      showToast('Placement deleted successfully!', 'success');
      setDeleteTarget(null);
      fetchData();
    } catch (err) {
      showToast(err.displayMessage || 'Failed to delete placement', 'error');
      setDeleteTarget(null);
    }
  };

  const clearFilters = () => { setStatusFilter(''); setCompanyFilter(''); };

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>📋 Placement Management</h2>
          <p>Track and manage all student placement records</p>
        </div>
        <div className="page-header-right">
          <button className="btn btn-primary" onClick={handleAdd}>＋ Add Placement</button>
        </div>
      </div>

      <div className="page-content">
        {/* Toolbar */}
        <div className="toolbar">
          <select className="filter-select" value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All Status</option>
            <option value="SELECTED">Selected</option>
            <option value="PENDING">Pending</option>
            <option value="REJECTED">Rejected</option>
          </select>
          <select className="filter-select" value={companyFilter}
            onChange={(e) => setCompanyFilter(e.target.value)}>
            <option value="">All Companies</option>
            {companies.map((c) => (
              <option key={c.id} value={c.id}>{c.companyName}</option>
            ))}
          </select>
          {(statusFilter || companyFilter) && (
            <button className="btn btn-secondary btn-sm" onClick={clearFilters}>✕ Clear</button>
          )}
        </div>

        {/* Table */}
        <div className="card" style={{ padding: 0 }}>
          {loading ? (
            <div className="loading-container"><div className="spinner" /></div>
          ) : placements.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">📋</div>
              <h3>No Placements Found</h3>
              <p>{statusFilter || companyFilter
                ? 'No placements match your filters.'
                : 'No placements added yet. Click "+ Add Placement" to start tracking.'}</p>
            </div>
          ) : (
            <>
              <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
                  Showing <strong style={{ color: 'var(--text-primary)' }}>{placements.length}</strong> placement(s)
                </span>
              </div>
              <div className="table-container" style={{ borderRadius: 0, border: 'none' }}>
                <table>
                  <thead>
                    <tr>
                      <th>ID</th><th>Student</th><th>Company</th>
                      <th>Job Role</th><th>Package</th><th>Date</th><th>Status</th><th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {placements.map((p) => (
                      <tr key={p.id}>
                        <td style={{ color: 'var(--text-muted)', fontWeight: 400 }}>#{p.id}</td>
                        <td>
                          <div>
                            <strong>{p.student?.studentName}</strong>
                            <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{p.student?.department}</div>
                          </div>
                        </td>
                        <td><strong>{p.company?.companyName}</strong></td>
                        <td>{p.jobRole}</td>
                        <td><span style={{ color: '#10b981', fontWeight: 700 }}>₹{p.packageLpa} LPA</span></td>
                        <td style={{ color: 'var(--text-muted)' }}>{p.placementDate}</td>
                        <td>{statusBadge(p.status)}</td>
                        <td>
                          <div style={{ display: 'flex', gap: 6 }}>
                            <button className="btn btn-secondary btn-sm" onClick={() => handleView(p)} title="View">👁️</button>
                            <button className="btn btn-secondary btn-sm" onClick={() => handleEdit(p)} title="Edit">✏️</button>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteClick(p)} title="Delete">🗑️</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </div>
      </div>

      <PlacementForm
        isOpen={formOpen}
        onClose={() => setFormOpen(false)}
        onSubmit={handleFormSubmit}
        editData={editData}
        students={students}
        companies={companies}
        loading={saving}
      />

      <DetailModal
        isOpen={!!viewData}
        onClose={() => setViewData(null)}
        icon="📋"
        title="Placement Details"
        subtitle={viewData ? `Placement #${viewData.id}` : ''}
        items={viewData ? [
          { label: 'Student', value: viewData.student?.studentName || '—' },
          { label: 'Student Dept', value: viewData.student?.department || '—' },
          { label: 'Company', value: viewData.company?.companyName || '—' },
          { label: 'Job Role', value: viewData.jobRole },
          { label: 'Package (LPA)', value: `₹${viewData.packageLpa} LPA` },
          { label: 'Placement Date', value: viewData.placementDate, type: 'date' },
          { label: 'Status', value: viewData.status === 'SELECTED' ? '✅ Selected'
              : viewData.status === 'REJECTED' ? '❌ Rejected' : '⏳ Pending' },
        ] : []}
      />

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Placement"
        message={<>Delete placement record for <strong>{deleteTarget?.student?.studentName}</strong> at <strong>{deleteTarget?.company?.companyName}</strong>?</>}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default Placements;
