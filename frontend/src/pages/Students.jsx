import React, { useState, useEffect, useCallback } from 'react';
import StudentForm from '../components/StudentForm';
import ConfirmDialog from '../components/ConfirmDialog';
import DetailModal from '../components/DetailModal';
import { studentService } from '../services/studentService';

const DEPARTMENTS = ['CSE', 'ECE', 'EEE', 'MECH', 'CIVIL', 'IT', 'AIDS', 'AIML', 'MBA', 'MCA'];

function placementBadge(status) {
  return status === 'PLACED'
    ? <span className="badge badge-success">✅ Placed</span>
    : <span className="badge badge-warning">⏳ Not Placed</span>;
}

function Students({ showToast }) {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [viewData, setViewData] = useState(null);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState('');
  const [deptFilter, setDeptFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const fetchStudents = useCallback(() => {
    setLoading(true);
    const params = {};
    if (search) params.search = search;
    if (deptFilter) params.department = deptFilter;
    if (statusFilter) params.status = statusFilter;
    studentService.getAll(params)
      .then((res) => setStudents(res.data))
      .catch(() => showToast('Failed to load students', 'error'))
      .finally(() => setLoading(false));
  }, [search, deptFilter, statusFilter, showToast]);

  useEffect(() => {
    const timer = setTimeout(fetchStudents, 300);
    return () => clearTimeout(timer);
  }, [fetchStudents]);

  const handleAdd = () => { setEditData(null); setFormOpen(true); };
  const handleEdit = (student) => { setEditData(student); setFormOpen(true); };
  const handleView = (student) => setViewData(student);
  const handleDeleteClick = (student) => setDeleteTarget(student);

  const handleFormSubmit = async (data) => {
    setSaving(true);
    try {
      if (editData) {
        await studentService.update(editData.id, data);
        showToast('Student updated successfully!', 'success');
      } else {
        await studentService.create(data);
        showToast('Student added successfully!', 'success');
      }
      setFormOpen(false);
      fetchStudents();
    } catch (err) {
      const msg = err.response?.data?.message || err.displayMessage || 'Failed to save student';
      showToast(msg, 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    try {
      await studentService.delete(deleteTarget.id);
      showToast('Student deleted successfully!', 'success');
      setDeleteTarget(null);
      fetchStudents();
    } catch (err) {
      showToast(err.displayMessage || 'Failed to delete student', 'error');
      setDeleteTarget(null);
    }
  };

  const handleClearFilters = () => {
    setSearch(''); setDeptFilter(''); setStatusFilter('');
  };

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>🎓 Student Management</h2>
          <p>Manage all student records and placement status</p>
        </div>
        <div className="page-header-right">
          <button className="btn btn-primary" onClick={handleAdd}>
            ＋ Add Student
          </button>
        </div>
      </div>

      <div className="page-content">
        {/* Toolbar */}
        <div className="toolbar">
          <div className="search-wrapper">
            <span className="search-icon">🔍</span>
            <input className="search-input" placeholder="Search by name, email, or department..."
              value={search} onChange={(e) => setSearch(e.target.value)} />
          </div>
          <select className="filter-select" value={deptFilter}
            onChange={(e) => setDeptFilter(e.target.value)}>
            <option value="">All Departments</option>
            {DEPARTMENTS.map((d) => <option key={d} value={d}>{d}</option>)}
          </select>
          <select className="filter-select" value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All Status</option>
            <option value="PLACED">Placed</option>
            <option value="NOT_PLACED">Not Placed</option>
          </select>
          {(search || deptFilter || statusFilter) && (
            <button className="btn btn-secondary btn-sm" onClick={handleClearFilters}>✕ Clear</button>
          )}
        </div>

        {/* Table */}
        <div className="card" style={{ padding: 0 }}>
          {loading ? (
            <div className="loading-container"><div className="spinner" /></div>
          ) : students.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">🎓</div>
              <h3>No Students Found</h3>
              <p>{search || deptFilter || statusFilter
                ? 'No students match your search/filter criteria.'
                : 'No students added yet. Click "+ Add Student" to get started.'}</p>
            </div>
          ) : (
            <>
              <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
                  Showing <strong style={{ color: 'var(--text-primary)' }}>{students.length}</strong> student(s)
                </span>
              </div>
              <div className="table-container" style={{ borderRadius: 0, border: 'none' }}>
                <table>
                  <thead>
                    <tr>
                      <th>ID</th><th>Name</th><th>Email</th><th>Department</th>
                      <th>Year</th><th>CGPA</th><th>Phone</th><th>Status</th><th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.map((s) => (
                      <tr key={s.id}>
                        <td style={{ color: 'var(--text-muted)', fontWeight: 400 }}>#{s.id}</td>
                        <td><strong>{s.studentName}</strong></td>
                        <td style={{ color: 'var(--text-muted)' }}>{s.email}</td>
                        <td><span className="badge badge-purple">{s.department}</span></td>
                        <td>Year {s.year}</td>
                        <td><span style={{ color: '#10b981', fontWeight: 700 }}>{s.cgpa}</span></td>
                        <td style={{ color: 'var(--text-muted)' }}>{s.phone}</td>
                        <td>{placementBadge(s.placementStatus)}</td>
                        <td>
                          <div style={{ display: 'flex', gap: 6 }}>
                            <button className="btn btn-secondary btn-sm" onClick={() => handleView(s)} title="View">👁️</button>
                            <button className="btn btn-secondary btn-sm" onClick={() => handleEdit(s)} title="Edit">✏️</button>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteClick(s)} title="Delete">🗑️</button>
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

      <StudentForm isOpen={formOpen} onClose={() => setFormOpen(false)}
        onSubmit={handleFormSubmit} editData={editData} loading={saving} />

      <DetailModal
        isOpen={!!viewData}
        onClose={() => setViewData(null)}
        icon="🎓"
        title="Student Details"
        subtitle={viewData ? `Student #${viewData.id}` : ''}
        items={viewData ? [
          { label: 'Name', value: viewData.studentName },
          { label: 'Email', value: viewData.email, type: 'mono' },
          { label: 'Department', value: viewData.department },
          { label: 'Year', value: `Year ${viewData.year}` },
          { label: 'CGPA', value: viewData.cgpa },
          { label: 'Phone', value: viewData.phone, type: 'mono' },
          { label: 'Skills', value: viewData.skills || '—' },
          { label: 'Placement Status', value: viewData.placementStatus === 'PLACED' ? '✅ Placed' : '⏳ Not Placed' },
        ] : []}
      />

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Student"
        message={<>Are you sure you want to delete <strong>{deleteTarget?.studentName}</strong>? This action cannot be undone.</>}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default Students;
