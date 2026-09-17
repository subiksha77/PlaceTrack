import React, { useState, useEffect } from 'react';

const initialForm = {
  certificationName: '', issuingOrganization: '', issueDate: '', credentialLink: '',
};

const blankToNull = (v) => {
  const trimmed = typeof v === 'string' ? v.trim() : v;
  return trimmed === '' || trimmed === undefined || trimmed === null ? null : trimmed;
};

function validate(form) {
  const errors = {};
  if (!form.certificationName.trim()) errors.certificationName = 'Certification name is required';
  else if (form.certificationName.trim().length > 150)
    errors.certificationName = 'Certification name must be at most 150 characters';

  if (!form.issuingOrganization.trim()) errors.issuingOrganization = 'Issuing organization is required';
  else if (form.issuingOrganization.trim().length > 120)
    errors.issuingOrganization = 'Issuing organization must be at most 120 characters';

  if (form.issueDate && form.issueDate > new Date().toISOString().slice(0, 10))
    errors.issueDate = 'Issue date cannot be in the future';

  if (form.credentialLink.trim() && form.credentialLink.trim().length > 300)
    errors.credentialLink = 'Credential link must be at most 300 characters';

  return errors;
}

function CertificationForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        certificationName: editData.certificationName || '',
        issuingOrganization: editData.issuingOrganization || '',
        issueDate: editData.issueDate || '',
        credentialLink: editData.credentialLink || '',
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
      certificationName: form.certificationName.trim(),
      issuingOrganization: form.issuingOrganization.trim(),
      issueDate: blankToNull(form.issueDate),
      credentialLink: blankToNull(form.credentialLink),
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Certification' : '➕ Add Certification'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group form-group-wide">
                <label className="form-label">Certification Name <span className="required">*</span></label>
                <input className={`form-input ${errors.certificationName ? 'error' : ''}`}
                  name="certificationName" value={form.certificationName} onChange={handleChange}
                  placeholder="e.g. AWS Certified Cloud Practitioner" />
                {errors.certificationName && <span className="form-error">⚠ {errors.certificationName}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Issuing Organization <span className="required">*</span></label>
                <input className={`form-input ${errors.issuingOrganization ? 'error' : ''}`}
                  name="issuingOrganization" value={form.issuingOrganization} onChange={handleChange}
                  placeholder="e.g. Amazon Web Services" />
                {errors.issuingOrganization && <span className="form-error">⚠ {errors.issuingOrganization}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Issue Date</label>
                <input className={`form-input ${errors.issueDate ? 'error' : ''}`}
                  name="issueDate" type="date" value={form.issueDate} onChange={handleChange} />
                {errors.issueDate && <span className="form-error">⚠ {errors.issueDate}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Credential Link</label>
                <input className={`form-input ${errors.credentialLink ? 'error' : ''}`}
                  name="credentialLink" value={form.credentialLink} onChange={handleChange}
                  placeholder="https://www.credly.com/badges/..." />
                {errors.credentialLink && <span className="form-error">⚠ {errors.credentialLink}</span>}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Certification' : '✔ Add Certification'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CertificationForm;
