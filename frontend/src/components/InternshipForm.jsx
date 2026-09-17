import React, { useState, useEffect } from 'react';

const initialForm = { companyName: '', role: '', duration: '', description: '' };

const blankToNull = (v) => {
  const trimmed = typeof v === 'string' ? v.trim() : v;
  return trimmed === '' || trimmed === undefined || trimmed === null ? null : trimmed;
};

function validate(form) {
  const errors = {};
  if (!form.companyName.trim()) errors.companyName = 'Company name is required';
  else if (form.companyName.trim().length > 120)
    errors.companyName = 'Company name must be at most 120 characters';

  if (!form.role.trim()) errors.role = 'Role is required';
  else if (form.role.trim().length > 120) errors.role = 'Role must be at most 120 characters';

  if (!form.duration.trim()) errors.duration = 'Duration is required';
  else if (form.duration.trim().length > 60) errors.duration = 'Duration must be at most 60 characters';

  if (form.description.length > 1000) errors.description = 'Description must be at most 1000 characters';

  return errors;
}

function InternshipForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        companyName: editData.companyName || '',
        role: editData.role || '',
        duration: editData.duration || '',
        description: editData.description || '',
      });
    } else {
      setForm(initialForm);
    }
    setErrors({});
  }, [editData, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validate(form);
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    onSubmit({
      companyName: form.companyName.trim(),
      role: form.role.trim(),
      duration: form.duration.trim(),
      description: blankToNull(form.description),
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Internship' : '➕ Add Internship'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Company Name <span className="required">*</span></label>
                <input className={`form-input ${errors.companyName ? 'error' : ''}`}
                  name="companyName" value={form.companyName} onChange={handleChange}
                  placeholder="e.g. Zoho Corporation" />
                {errors.companyName && <span className="form-error">⚠ {errors.companyName}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Role <span className="required">*</span></label>
                <input className={`form-input ${errors.role ? 'error' : ''}`}
                  name="role" value={form.role} onChange={handleChange}
                  placeholder="e.g. Frontend Developer Intern" />
                {errors.role && <span className="form-error">⚠ {errors.role}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Duration <span className="required">*</span></label>
                <input className={`form-input ${errors.duration ? 'error' : ''}`}
                  name="duration" value={form.duration} onChange={handleChange}
                  placeholder="e.g. Jun 2025 – Aug 2025 (3 months)" />
                {errors.duration && <span className="form-error">⚠ {errors.duration}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Description</label>
                <textarea className={`form-input ${errors.description ? 'error' : ''}`}
                  name="description" rows="3" value={form.description} onChange={handleChange}
                  placeholder="What you worked on and what you delivered..." />
                {errors.description && <span className="form-error">⚠ {errors.description}</span>}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Internship' : '✔ Add Internship'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default InternshipForm;
