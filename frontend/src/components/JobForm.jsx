import React, { useState, useEffect } from 'react';

export const JOB_TYPES = ['FULL_TIME', 'INTERNSHIP', 'CONTRACT', 'CONTRACT_INTERNSHIP', 'WFH', 'OTHER'];
export const JOB_STATUSES = ['OPEN', 'CLOSED', 'CANCELLED', 'COMPLETED'];

const initialForm = {
  companyId: '', jobRole: '', jobDescription: '', packageLpa: '', jobType: 'FULL_TIME',
  workLocation: '', minCgpa: '', eligibleDepartments: '', eligibleYear: '', graduationYear: '',
  allowedBacklogs: '', requiredSkills: '', preferredSkills: '', driveDate: '',
  applicationDeadline: '', selectionProcess: '', status: 'OPEN',
};

const blankToNull = (v) => {
  const trimmed = typeof v === 'string' ? v.trim() : v;
  return trimmed === '' || trimmed === undefined || trimmed === null ? null : trimmed;
};

const numberOrNull = (v) => (v === '' || v === null || v === undefined ? null : Number(v));

function validate(form) {
  const errors = {};
  if (!form.companyId) errors.companyId = 'Company is required';
  if (!form.jobRole.trim()) errors.jobRole = 'Job role is required';
  else if (form.jobRole.trim().length > 200) errors.jobRole = 'Job role must be at most 200 characters';

  if (form.packageLpa !== '' && Number(form.packageLpa) < 0) errors.packageLpa = 'Package cannot be negative';
  if (form.minCgpa !== '' && (Number(form.minCgpa) < 0 || Number(form.minCgpa) > 10))
    errors.minCgpa = 'Minimum CGPA must be 0–10';
  if (form.eligibleYear !== '' && !/^\d+$/.test(form.eligibleYear.trim()))
    errors.eligibleYear = 'Eligible year must be a number';
  if (form.graduationYear !== '' && !/^\d{4}$/.test(form.graduationYear.trim()))
    errors.graduationYear = 'Graduation year must be a 4-digit year';
  if (form.allowedBacklogs !== '' && Number(form.allowedBacklogs) < 0)
    errors.allowedBacklogs = 'Allowed backlogs cannot be negative';
  if (form.eligibleDepartments.length > 200) errors.eligibleDepartments = 'At most 200 characters';
  if (form.requiredSkills.length > 300) errors.requiredSkills = 'At most 300 characters';
  if (form.preferredSkills.length > 300) errors.preferredSkills = 'At most 300 characters';
  if (form.selectionProcess.length > 500) errors.selectionProcess = 'At most 500 characters';

  return errors;
}

function JobForm({ isOpen, onClose, onSubmit, editData, companies = [], loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        companyId: editData.companyId?.toString() || '',
        jobRole: editData.jobRole || '',
        jobDescription: editData.jobDescription || '',
        packageLpa: editData.packageLpa?.toString() || '',
        jobType: editData.jobType || 'FULL_TIME',
        workLocation: editData.workLocation || '',
        minCgpa: editData.minCgpa?.toString() || '',
        eligibleDepartments: editData.eligibleDepartments || '',
        eligibleYear: editData.eligibleYear?.toString() || '',
        graduationYear: editData.graduationYear?.toString() || '',
        allowedBacklogs: editData.allowedBacklogs?.toString() || '',
        requiredSkills: editData.requiredSkills || '',
        preferredSkills: editData.preferredSkills || '',
        driveDate: editData.driveDate || '',
        applicationDeadline: editData.applicationDeadline || '',
        selectionProcess: editData.selectionProcess || '',
        status: editData.status || 'OPEN',
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
      companyId: Number(form.companyId),
      jobRole: form.jobRole.trim(),
      jobDescription: blankToNull(form.jobDescription),
      packageLpa: numberOrNull(form.packageLpa),
      jobType: form.jobType || null,
      workLocation: blankToNull(form.workLocation),
      minCgpa: numberOrNull(form.minCgpa),
      eligibleDepartments: blankToNull(form.eligibleDepartments),
      eligibleYear: blankToNull(form.eligibleYear),
      graduationYear: blankToNull(form.graduationYear),
      allowedBacklogs: numberOrNull(form.allowedBacklogs),
      requiredSkills: blankToNull(form.requiredSkills),
      preferredSkills: blankToNull(form.preferredSkills),
      driveDate: blankToNull(form.driveDate),
      applicationDeadline: blankToNull(form.applicationDeadline),
      selectionProcess: blankToNull(form.selectionProcess),
      status: form.status || null,
    });
  };

  const text = (name, label, placeholder, hint) => (
    <div className="form-group">
      <label className="form-label">{label}</label>
      <input className={`form-input ${errors[name] ? 'error' : ''}`}
        name={name} value={form[name]} onChange={handleChange} placeholder={placeholder} />
      {errors[name] && <span className="form-error">⚠ {errors[name]}</span>}
      {!errors[name] && hint && <span className="form-hint">{hint}</span>}
    </div>
  );

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal modal-lg" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Job Opportunity' : '➕ Add Job Opportunity'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <p className="form-section-title">🏢 Role Details</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Company <span className="required">*</span></label>
                <select className={`form-input ${errors.companyId ? 'error' : ''}`}
                  name="companyId" value={form.companyId} onChange={handleChange}>
                  <option value="">Select company</option>
                  {companies.map((c) => (
                    <option key={c.id} value={c.id}>{c.companyName}</option>
                  ))}
                </select>
                {errors.companyId && <span className="form-error">⚠ {errors.companyId}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Job Role <span className="required">*</span></label>
                <input className={`form-input ${errors.jobRole ? 'error' : ''}`}
                  name="jobRole" value={form.jobRole} onChange={handleChange}
                  placeholder="e.g. Software Engineer Trainee" />
                {errors.jobRole && <span className="form-error">⚠ {errors.jobRole}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Job Type</label>
                <select className="form-input" name="jobType" value={form.jobType} onChange={handleChange}>
                  {JOB_TYPES.map((t) => <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>)}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Status</label>
                <select className="form-input" name="status" value={form.status} onChange={handleChange}>
                  {JOB_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Package (LPA)</label>
                <input className={`form-input ${errors.packageLpa ? 'error' : ''}`}
                  name="packageLpa" type="number" step="0.01" min="0"
                  value={form.packageLpa} onChange={handleChange} placeholder="e.g. 6.5" />
                {errors.packageLpa && <span className="form-error">⚠ {errors.packageLpa}</span>}
              </div>

              {text('workLocation', 'Work Location', 'e.g. Chennai / Bengaluru')}
            </div>

            <p className="form-section-title">🎯 Eligibility Criteria</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Minimum CGPA</label>
                <input className={`form-input ${errors.minCgpa ? 'error' : ''}`}
                  name="minCgpa" type="number" step="0.01" min="0" max="10"
                  value={form.minCgpa} onChange={handleChange} placeholder="e.g. 7.0" />
                {errors.minCgpa && <span className="form-error">⚠ {errors.minCgpa}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Allowed Backlogs</label>
                <input className={`form-input ${errors.allowedBacklogs ? 'error' : ''}`}
                  name="allowedBacklogs" type="number" min="0"
                  value={form.allowedBacklogs} onChange={handleChange} placeholder="e.g. 0" />
                {errors.allowedBacklogs && <span className="form-error">⚠ {errors.allowedBacklogs}</span>}
              </div>

              {text('eligibleYear', 'Eligible Year (of study)', 'e.g. 4', 'Year of study the drive is open to')}
              {text('graduationYear', 'Graduation Year', 'e.g. 2026')}

              <div className="form-group form-group-wide">
                <label className="form-label">Eligible Departments</label>
                <input className={`form-input ${errors.eligibleDepartments ? 'error' : ''}`}
                  name="eligibleDepartments" value={form.eligibleDepartments} onChange={handleChange}
                  placeholder="e.g. CSE, IT, ECE" />
                {errors.eligibleDepartments && <span className="form-error">⚠ {errors.eligibleDepartments}</span>}
                {!errors.eligibleDepartments &&
                  <span className="form-hint">Comma-separated. Leave blank to allow all departments.</span>}
              </div>
            </div>

            <p className="form-section-title">📅 Dates, Skills &amp; Process</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Drive Date</label>
                <input className="form-input" name="driveDate" type="date"
                  value={form.driveDate} onChange={handleChange} />
              </div>

              <div className="form-group">
                <label className="form-label">Application Deadline</label>
                <input className="form-input" name="applicationDeadline" type="date"
                  value={form.applicationDeadline} onChange={handleChange} />
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Required Skills</label>
                <input className={`form-input ${errors.requiredSkills ? 'error' : ''}`}
                  name="requiredSkills" value={form.requiredSkills} onChange={handleChange}
                  placeholder="e.g. Java, Spring Boot, SQL" />
                {errors.requiredSkills && <span className="form-error">⚠ {errors.requiredSkills}</span>}
                {!errors.requiredSkills &&
                  <span className="form-hint">Comma-separated. Used by the matching engine to score students.</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Preferred Skills</label>
                <input className={`form-input ${errors.preferredSkills ? 'error' : ''}`}
                  name="preferredSkills" value={form.preferredSkills} onChange={handleChange}
                  placeholder="e.g. React, Docker" />
                {errors.preferredSkills && <span className="form-error">⚠ {errors.preferredSkills}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Selection Process</label>
                <input className={`form-input ${errors.selectionProcess ? 'error' : ''}`}
                  name="selectionProcess" value={form.selectionProcess} onChange={handleChange}
                  placeholder="e.g. Aptitude → Technical → HR" />
                {errors.selectionProcess && <span className="form-error">⚠ {errors.selectionProcess}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Job Description</label>
                <textarea className="form-input" name="jobDescription" rows="3"
                  value={form.jobDescription} onChange={handleChange}
                  placeholder="Responsibilities, tech stack, bond details..." />
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Job' : '✔ Add Job'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default JobForm;
