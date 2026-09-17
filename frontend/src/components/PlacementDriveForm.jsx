import React, { useState, useEffect, useMemo } from 'react';

export const DRIVE_STATUSES = ['UPCOMING', 'OPEN', 'CLOSED', 'COMPLETED', 'ONBOARDING'];

const initialForm = {
  companyId: '', jobOpportunityId: '', jobRole: '', packageLpa: '', minCgpa: '',
  eligibleDepartments: '', eligibleYear: '', graduationYear: '', allowedBacklogs: '',
  requiredSkills: '', preferredSkills: '', driveDate: '', applicationDeadline: '',
  workLocation: '', selectionProcess: '', status: 'UPCOMING',
};

const blankToNull = (v) => {
  const trimmed = typeof v === 'string' ? v.trim() : v;
  return trimmed === '' || trimmed === undefined || trimmed === null ? null : trimmed;
};

const numberOrNull = (v) => (v === '' || v === null || v === undefined ? null : Number(v));

function validate(form) {
  const errors = {};
  if (!form.companyId) errors.companyId = 'Company is required';
  if (!form.jobOpportunityId) errors.jobOpportunityId = 'Job opportunity is required';
  if (!form.driveDate) errors.driveDate = 'Drive date is required';
  if (!form.applicationDeadline) errors.applicationDeadline = 'Application deadline is required';

  if (form.driveDate && form.applicationDeadline && form.applicationDeadline > form.driveDate)
    errors.applicationDeadline = 'Deadline must be on or before the drive date';

  if (form.packageLpa !== '' && Number(form.packageLpa) < 0) errors.packageLpa = 'Package cannot be negative';
  if (form.minCgpa !== '' && (Number(form.minCgpa) < 0 || Number(form.minCgpa) > 10))
    errors.minCgpa = 'Minimum CGPA must be 0–10';
  if (form.allowedBacklogs !== '' && Number(form.allowedBacklogs) < 0)
    errors.allowedBacklogs = 'Allowed backlogs cannot be negative';
  if (form.eligibleDepartments.length > 200) errors.eligibleDepartments = 'At most 200 characters';
  if (form.requiredSkills.length > 300) errors.requiredSkills = 'At most 300 characters';
  if (form.preferredSkills.length > 300) errors.preferredSkills = 'At most 300 characters';
  if (form.selectionProcess.length > 500) errors.selectionProcess = 'At most 500 characters';

  return errors;
}

function PlacementDriveForm({ isOpen, onClose, onSubmit, editData, companies = [], jobs = [], loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const companyJobs = useMemo(
    () => jobs.filter((j) => j.companyId?.toString() === form.companyId?.toString()),
    [jobs, form.companyId]
  );

  useEffect(() => {
    if (editData) {
      const matchedJob = jobs.find(
        (j) => j.companyId?.toString() === editData.companyId?.toString() && j.jobRole === editData.jobRole
      );
      setForm({
        companyId: editData.companyId?.toString() || '',
        jobOpportunityId: (editData.jobOpportunityId || matchedJob?.id)?.toString() || '',
        jobRole: editData.jobRole || '',
        packageLpa: editData.packageLpa?.toString() || '',
        minCgpa: editData.minCgpa?.toString() || '',
        eligibleDepartments: editData.eligibleDepartments || '',
        eligibleYear: editData.eligibleYear?.toString() || '',
        graduationYear: editData.graduationYear?.toString() || '',
        allowedBacklogs: editData.allowedBacklogs?.toString() || '',
        requiredSkills: editData.requiredSkills || '',
        preferredSkills: editData.preferredSkills || '',
        driveDate: editData.driveDate || '',
        applicationDeadline: editData.applicationDeadline || '',
        workLocation: editData.driveLocation || editData.workLocation || '',
        selectionProcess: editData.selectionProcess || '',
        status: editData.status || 'UPCOMING',
      });
    } else {
      setForm(initialForm);
    }
    setErrors({});
  }, [editData, isOpen, jobs]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  // Selecting a company resets the linked job; selecting a job prefills the
  // role and criteria so the drive starts from the job's own requirements.
  const handleCompanyChange = (e) => {
    const value = e.target.value;
    setForm((prev) => ({ ...prev, companyId: value, jobOpportunityId: '', jobRole: '' }));
    setErrors((prev) => ({ ...prev, companyId: '', jobOpportunityId: '' }));
  };

  const handleJobChange = (e) => {
    const jobId = e.target.value;
    const job = jobs.find((j) => j.id?.toString() === jobId);
    setForm((prev) => ({
      ...prev,
      jobOpportunityId: jobId,
      jobRole: job?.jobRole || prev.jobRole,
      packageLpa: job?.packageLpa?.toString() || '',
      minCgpa: job?.minCgpa?.toString() || '',
      eligibleDepartments: job?.eligibleDepartments || '',
      eligibleYear: job?.eligibleYear || '',
      graduationYear: job?.graduationYear || '',
      allowedBacklogs: job?.allowedBacklogs?.toString() || '',
      requiredSkills: job?.requiredSkills || '',
      preferredSkills: job?.preferredSkills || '',
      workLocation: job?.workLocation || prev.workLocation,
      selectionProcess: job?.selectionProcess || prev.selectionProcess,
    }));
    setErrors((prev) => ({ ...prev, jobOpportunityId: '' }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validate(form);
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    onSubmit({
      companyId: Number(form.companyId),
      jobOpportunityId: Number(form.jobOpportunityId),
      jobRole: blankToNull(form.jobRole),
      packageLpa: numberOrNull(form.packageLpa),
      minCgpa: numberOrNull(form.minCgpa),
      eligibleDepartments: blankToNull(form.eligibleDepartments),
      eligibleYear: blankToNull(form.eligibleYear),
      graduationYear: blankToNull(form.graduationYear),
      allowedBacklogs: numberOrNull(form.allowedBacklogs),
      requiredSkills: blankToNull(form.requiredSkills),
      preferredSkills: blankToNull(form.preferredSkills),
      driveDate: form.driveDate,
      applicationDeadline: form.applicationDeadline,
      workLocation: blankToNull(form.workLocation),
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
          <h3>{editData ? '✏️ Edit Placement Drive' : '➕ Schedule Placement Drive'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <p className="form-section-title">🏢 Company &amp; Role</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Company <span className="required">*</span></label>
                <select className={`form-input ${errors.companyId ? 'error' : ''}`}
                  name="companyId" value={form.companyId} onChange={handleCompanyChange}>
                  <option value="">Select company</option>
                  {companies.map((c) => (
                    <option key={c.id} value={c.id}>{c.companyName}</option>
                  ))}
                </select>
                {errors.companyId && <span className="form-error">⚠ {errors.companyId}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Job Opportunity <span className="required">*</span></label>
                <select className={`form-input ${errors.jobOpportunityId ? 'error' : ''}`}
                  name="jobOpportunityId" value={form.jobOpportunityId} onChange={handleJobChange}
                  disabled={!form.companyId}>
                  <option value="">
                    {form.companyId ? 'Select job opportunity' : 'Select a company first'}
                  </option>
                  {companyJobs.map((j) => (
                    <option key={j.id} value={j.id}>{j.jobRole}</option>
                  ))}
                </select>
                {errors.jobOpportunityId && <span className="form-error">⚠ {errors.jobOpportunityId}</span>}
                {!errors.jobOpportunityId && form.companyId && companyJobs.length === 0 &&
                  <span className="form-hint">This company has no job opportunities yet — add one first.</span>}
              </div>

              {text('jobRole', 'Drive Title / Role', 'e.g. Software Engineer Trainee')}
              {text('workLocation', 'Drive Location', 'e.g. Main Campus, Block C')}
            </div>

            <p className="form-section-title">📅 Schedule</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Drive Date <span className="required">*</span></label>
                <input className={`form-input ${errors.driveDate ? 'error' : ''}`}
                  name="driveDate" type="date" value={form.driveDate} onChange={handleChange} />
                {errors.driveDate && <span className="form-error">⚠ {errors.driveDate}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Application Deadline <span className="required">*</span></label>
                <input className={`form-input ${errors.applicationDeadline ? 'error' : ''}`}
                  name="applicationDeadline" type="date" value={form.applicationDeadline} onChange={handleChange} />
                {errors.applicationDeadline && <span className="form-error">⚠ {errors.applicationDeadline}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Status</label>
                <select className="form-input" name="status" value={form.status} onChange={handleChange}>
                  {DRIVE_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>

              {text('selectionProcess', 'Selection Process', 'e.g. Aptitude → Technical → HR')}
            </div>

            <p className="form-section-title">🎯 Eligibility &amp; Skills</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Package (LPA)</label>
                <input className={`form-input ${errors.packageLpa ? 'error' : ''}`}
                  name="packageLpa" type="number" step="0.01" min="0"
                  value={form.packageLpa} onChange={handleChange} placeholder="e.g. 6.5" />
                {errors.packageLpa && <span className="form-error">⚠ {errors.packageLpa}</span>}
              </div>

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

              {text('eligibleYear', 'Eligible Year (of study)', 'e.g. 4')}
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

              <div className="form-group form-group-wide">
                <label className="form-label">Required Skills</label>
                <input className={`form-input ${errors.requiredSkills ? 'error' : ''}`}
                  name="requiredSkills" value={form.requiredSkills} onChange={handleChange}
                  placeholder="e.g. Java, Spring Boot, SQL" />
                {errors.requiredSkills && <span className="form-error">⚠ {errors.requiredSkills}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Preferred Skills</label>
                <input className={`form-input ${errors.preferredSkills ? 'error' : ''}`}
                  name="preferredSkills" value={form.preferredSkills} onChange={handleChange}
                  placeholder="e.g. React, Docker" />
                {errors.preferredSkills && <span className="form-error">⚠ {errors.preferredSkills}</span>}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Drive' : '✔ Schedule Drive'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PlacementDriveForm;
