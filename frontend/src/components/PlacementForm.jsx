import React, { useState, useEffect } from 'react';

const initialForm = {
  studentId: '', companyId: '', jobRole: '', packageLpa: '', placementDate: '', status: 'SELECTED',
};

function validate(form) {
  const errors = {};
  if (!form.studentId) errors.studentId = 'Student is required';
  if (!form.companyId) errors.companyId = 'Company is required';
  if (!form.jobRole.trim()) errors.jobRole = 'Job role is required';
  if (form.packageLpa === '') errors.packageLpa = 'Package is required';
  else if (Number(form.packageLpa) < 0) errors.packageLpa = 'Package cannot be negative';
  if (!form.placementDate) errors.placementDate = 'Placement date is required';
  return errors;
}

function PlacementForm({ isOpen, onClose, onSubmit, editData, students, companies, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        studentId: editData.student?.id?.toString() || '',
        companyId: editData.company?.id?.toString() || '',
        jobRole: editData.jobRole || '',
        packageLpa: editData.packageLpa?.toString() || '',
        placementDate: editData.placementDate || '',
        status: editData.status || 'SELECTED',
      });
    } else {
      setForm(initialForm);
    }
    setErrors({});
  }, [editData, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
    if (name === 'companyId') {
      const company = companies.find((c) => c.id?.toString() === value);
      setForm((prev) => {
        const next = { ...prev, companyId: value };
        // Auto-fill job role and package from the selected company when empty
        if (company) {
          if (!prev.jobRole.trim()) next.jobRole = company.jobRole || '';
          if (prev.packageLpa === '' && company.packageLpa != null) next.packageLpa = company.packageLpa.toString();
        }
        return next;
      });
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validate(form);
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    onSubmit({
      student: { id: Number(form.studentId) },
      company: { id: Number(form.companyId) },
      jobRole: form.jobRole,
      packageLpa: Number(form.packageLpa),
      placementDate: form.placementDate,
      status: form.status,
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Placement' : '➕ Add New Placement'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Student <span className="required">*</span></label>
                <select className={`form-select ${errors.studentId ? 'error' : ''}`}
                  name="studentId" value={form.studentId} onChange={handleChange}>
                  <option value="">{students.length === 0 ? '— No students available —' : '-- Select Student --'}</option>
                  {students.map((s) => (
                    <option key={s.id} value={s.id}>{s.studentName} ({s.email})</option>
                  ))}
                </select>
                {errors.studentId && <span className="form-error">⚠ {errors.studentId}</span>}
                {students.length === 0 && (
                  <span className="form-hint">No students found. Add students on the Students page first.</span>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">Company <span className="required">*</span></label>
                <select className={`form-select ${errors.companyId ? 'error' : ''}`}
                  name="companyId" value={form.companyId} onChange={handleChange}>
                  <option value="">{companies.length === 0 ? '— No companies available —' : '-- Select Company --'}</option>
                  {companies.map((c) => (
                    <option key={c.id} value={c.id}>{c.companyName} — {c.jobRole}</option>
                  ))}
                </select>
                {errors.companyId && <span className="form-error">⚠ {errors.companyId}</span>}
                {companies.length === 0 && (
                  <span className="form-hint">No companies found. Add companies on the Companies page first.</span>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">Job Role <span className="required">*</span></label>
                <input className={`form-input ${errors.jobRole ? 'error' : ''}`}
                  name="jobRole" value={form.jobRole} onChange={handleChange}
                  placeholder="e.g. Software Engineer" />
                {errors.jobRole && <span className="form-error">⚠ {errors.jobRole}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Package (LPA) <span className="required">*</span></label>
                <input className={`form-input ${errors.packageLpa ? 'error' : ''}`}
                  name="packageLpa" type="number" step="0.1" min="0"
                  value={form.packageLpa} onChange={handleChange} placeholder="e.g. 8.0" />
                {errors.packageLpa && <span className="form-error">⚠ {errors.packageLpa}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Placement Date <span className="required">*</span></label>
                <input className={`form-input ${errors.placementDate ? 'error' : ''}`}
                  name="placementDate" type="date" value={form.placementDate} onChange={handleChange} />
                {errors.placementDate && <span className="form-error">⚠ {errors.placementDate}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Status</label>
                <select className="form-select" name="status" value={form.status} onChange={handleChange}>
                  <option value="SELECTED">Selected</option>
                  <option value="PENDING">Pending</option>
                  <option value="REJECTED">Rejected</option>
                </select>
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Placement' : '✔ Add Placement'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PlacementForm;
