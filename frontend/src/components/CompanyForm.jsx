import React, { useState, useEffect } from 'react';

const initialForm = {
  companyName: '', jobRole: '', packageLpa: '', eligibilityCgpa: '', driveDate: '', location: '',
};

function validate(form) {
  const errors = {};
  if (!form.companyName.trim()) errors.companyName = 'Company name is required';
  else if (form.companyName.trim().length < 2) errors.companyName = 'Company name too short';

  if (!form.jobRole.trim()) errors.jobRole = 'Job role is required';

  if (form.packageLpa === '') errors.packageLpa = 'Package is required';
  else if (Number(form.packageLpa) < 0) errors.packageLpa = 'Package cannot be negative';

  if (form.eligibilityCgpa === '') errors.eligibilityCgpa = 'Eligibility CGPA is required';
  else if (Number(form.eligibilityCgpa) < 0 || Number(form.eligibilityCgpa) > 10)
    errors.eligibilityCgpa = 'CGPA must be 0–10';

  if (!form.driveDate) errors.driveDate = 'Drive date is required';

  if (!form.location.trim()) errors.location = 'Location is required';

  return errors;
}

function CompanyForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        companyName: editData.companyName || '',
        jobRole: editData.jobRole || '',
        packageLpa: editData.packageLpa?.toString() || '',
        eligibilityCgpa: editData.eligibilityCgpa?.toString() || '',
        driveDate: editData.driveDate || '',
        location: editData.location || '',
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
      ...form,
      packageLpa: Number(form.packageLpa),
      eligibilityCgpa: Number(form.eligibilityCgpa),
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Company' : '➕ Add New Company'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Company Name <span className="required">*</span></label>
                <input className={`form-input ${errors.companyName ? 'error' : ''}`}
                  name="companyName" value={form.companyName} onChange={handleChange}
                  placeholder="e.g. TCS, Infosys" />
                {errors.companyName && <span className="form-error">⚠ {errors.companyName}</span>}
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
                  value={form.packageLpa} onChange={handleChange} placeholder="e.g. 6.5" />
                {errors.packageLpa && <span className="form-error">⚠ {errors.packageLpa}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Eligibility CGPA <span className="required">*</span></label>
                <input className={`form-input ${errors.eligibilityCgpa ? 'error' : ''}`}
                  name="eligibilityCgpa" type="number" step="0.1" min="0" max="10"
                  value={form.eligibilityCgpa} onChange={handleChange} placeholder="e.g. 7.0" />
                {errors.eligibilityCgpa && <span className="form-error">⚠ {errors.eligibilityCgpa}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Drive Date <span className="required">*</span></label>
                <input className={`form-input ${errors.driveDate ? 'error' : ''}`}
                  name="driveDate" type="date" value={form.driveDate} onChange={handleChange} />
                {errors.driveDate && <span className="form-error">⚠ {errors.driveDate}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Location <span className="required">*</span></label>
                <input className={`form-input ${errors.location ? 'error' : ''}`}
                  name="location" value={form.location} onChange={handleChange}
                  placeholder="e.g. Chennai, Bangalore" />
                {errors.location && <span className="form-error">⚠ {errors.location}</span>}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Company' : '✔ Add Company'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CompanyForm;
