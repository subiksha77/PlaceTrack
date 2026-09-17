import React, { useState, useEffect } from 'react';

const initialForm = {
  studentName: '', phone: '', degree: '', registerNumber: '', dateOfBirth: '', gender: '',
  cgpa: '', graduationYear: '', tenthPercentage: '', twelfthPercentage: '', backlogs: '',
  location: '', city: '', state: '', address: '',
  linkedIn: '', gitHub: '', codingProfiles: '', skills: '', achievements: '',
};

/** Blank inputs must be sent as null - the backend validates patterns and lengths. */
const blankToNull = (v) => {
  if (v === undefined || v === null) return null;
  const trimmed = typeof v === 'string' ? v.trim() : v;
  return trimmed === '' ? null : trimmed;
};

const numberOrNull = (v) => (v === '' || v === null || v === undefined ? null : Number(v));

function validate(form) {
  const errors = {};
  if (!form.studentName.trim()) errors.studentName = 'Full name is required';
  else if (form.studentName.trim().length > 100) errors.studentName = 'Name must be at most 100 characters';

  if (form.phone.trim() && !/^[6-9]\d{9}$/.test(form.phone.trim()))
    errors.phone = 'Enter a valid 10-digit mobile number';

  if (form.cgpa !== '' && (Number(form.cgpa) < 0 || Number(form.cgpa) > 10))
    errors.cgpa = 'CGPA must be 0–10';

  if (form.graduationYear !== '' &&
    (Number(form.graduationYear) < 2000 || Number(form.graduationYear) > 2100))
    errors.graduationYear = 'Graduation year must be between 2000 and 2100';

  if (form.tenthPercentage !== '' && (Number(form.tenthPercentage) < 0 || Number(form.tenthPercentage) > 100))
    errors.tenthPercentage = '10th % must be 0–100';

  if (form.twelfthPercentage !== '' && (Number(form.twelfthPercentage) < 0 || Number(form.twelfthPercentage) > 100))
    errors.twelfthPercentage = '12th % must be 0–100';

  if (form.backlogs !== '' && Number(form.backlogs) < 0) errors.backlogs = 'Backlogs cannot be negative';

  if (form.dateOfBirth && form.dateOfBirth >= new Date().toISOString().slice(0, 10))
    errors.dateOfBirth = 'Date of birth must be in the past';

  return errors;
}

function ProfileForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        studentName: editData.studentName || '',
        phone: editData.phone || '',
        degree: editData.degree || '',
        registerNumber: editData.registerNumber || '',
        dateOfBirth: editData.dateOfBirth || '',
        gender: editData.gender || '',
        cgpa: editData.cgpa?.toString() || '',
        graduationYear: editData.graduationYear?.toString() || '',
        tenthPercentage: editData.tenthPercentage?.toString() || '',
        twelfthPercentage: editData.twelfthPercentage?.toString() || '',
        backlogs: editData.backlogs?.toString() || '',
        location: editData.location || '',
        city: editData.city || '',
        state: editData.state || '',
        address: editData.address || '',
        linkedIn: editData.linkedIn || '',
        gitHub: editData.gitHub || '',
        codingProfiles: editData.codingProfiles || '',
        skills: editData.skills || '',
        achievements: editData.achievements || '',
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
      studentName: form.studentName.trim(),
      phone: blankToNull(form.phone),
      degree: blankToNull(form.degree),
      registerNumber: blankToNull(form.registerNumber),
      dateOfBirth: blankToNull(form.dateOfBirth),
      gender: blankToNull(form.gender),
      cgpa: numberOrNull(form.cgpa),
      graduationYear: numberOrNull(form.graduationYear),
      tenthPercentage: numberOrNull(form.tenthPercentage),
      twelfthPercentage: numberOrNull(form.twelfthPercentage),
      backlogs: numberOrNull(form.backlogs),
      location: blankToNull(form.location),
      city: blankToNull(form.city),
      state: blankToNull(form.state),
      address: blankToNull(form.address),
      linkedIn: blankToNull(form.linkedIn),
      gitHub: blankToNull(form.gitHub),
      codingProfiles: blankToNull(form.codingProfiles),
      skills: blankToNull(form.skills),
      achievements: blankToNull(form.achievements),
    });
  };

  const text = (name, label, placeholder, extra = {}) => (
    <div className="form-group">
      <label className="form-label">{label}</label>
      <input className={`form-input ${errors[name] ? 'error' : ''}`}
        name={name} value={form[name]} onChange={handleChange}
        placeholder={placeholder} {...extra} />
      {errors[name] && <span className="form-error">⚠ {errors[name]}</span>}
    </div>
  );

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal modal-lg" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>✏️ Edit Placement Profile</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <p className="form-section-title">👤 Personal Information</p>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Full Name <span className="required">*</span></label>
                <input className={`form-input ${errors.studentName ? 'error' : ''}`}
                  name="studentName" value={form.studentName} onChange={handleChange}
                  placeholder="e.g. Anitha Raman" />
                {errors.studentName && <span className="form-error">⚠ {errors.studentName}</span>}
              </div>

              {text('phone', 'Phone', 'e.g. 9876543210', { type: 'tel', maxLength: 10 })}
              {text('dateOfBirth', 'Date of Birth', '', { type: 'date' })}

              <div className="form-group">
                <label className="form-label">Gender</label>
                <select className="form-input" name="gender" value={form.gender} onChange={handleChange}>
                  <option value="">Not specified</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              {text('address', 'Address', 'e.g. 12, Anna Nagar')}
              {text('city', 'City', 'e.g. Chennai')}
              {text('state', 'State', 'e.g. Tamil Nadu')}
              {text('location', 'Preferred Location', 'e.g. Chennai / Remote')}
            </div>

            <p className="form-section-title">🎓 Academic Information</p>
            <div className="form-grid">
              {text('degree', 'Degree', 'e.g. B.E. Computer Science')}
              {text('registerNumber', 'Register Number', 'e.g. 21CS1045')}

              <div className="form-group">
                <label className="form-label">CGPA</label>
                <input className={`form-input ${errors.cgpa ? 'error' : ''}`}
                  name="cgpa" type="number" step="0.01" min="0" max="10"
                  value={form.cgpa} onChange={handleChange} placeholder="e.g. 8.4" />
                {errors.cgpa && <span className="form-error">⚠ {errors.cgpa}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Graduation Year</label>
                <input className={`form-input ${errors.graduationYear ? 'error' : ''}`}
                  name="graduationYear" type="number" min="2000" max="2100"
                  value={form.graduationYear} onChange={handleChange} placeholder="e.g. 2026" />
                {errors.graduationYear && <span className="form-error">⚠ {errors.graduationYear}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">10th Percentage</label>
                <input className={`form-input ${errors.tenthPercentage ? 'error' : ''}`}
                  name="tenthPercentage" type="number" step="0.01" min="0" max="100"
                  value={form.tenthPercentage} onChange={handleChange} placeholder="e.g. 88.5" />
                {errors.tenthPercentage && <span className="form-error">⚠ {errors.tenthPercentage}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">12th Percentage</label>
                <input className={`form-input ${errors.twelfthPercentage ? 'error' : ''}`}
                  name="twelfthPercentage" type="number" step="0.01" min="0" max="100"
                  value={form.twelfthPercentage} onChange={handleChange} placeholder="e.g. 90.2" />
                {errors.twelfthPercentage && <span className="form-error">⚠ {errors.twelfthPercentage}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Active Backlogs</label>
                <input className={`form-input ${errors.backlogs ? 'error' : ''}`}
                  name="backlogs" type="number" min="0"
                  value={form.backlogs} onChange={handleChange} placeholder="e.g. 0" />
                {errors.backlogs && <span className="form-error">⚠ {errors.backlogs}</span>}
              </div>
            </div>

            <p className="form-section-title">🔗 Profiles &amp; Highlights</p>
            <div className="form-grid">
              {text('linkedIn', 'LinkedIn URL', 'https://linkedin.com/in/...')}
              {text('gitHub', 'GitHub URL', 'https://github.com/...')}

              <div className="form-group form-group-wide">
                <label className="form-label">Coding Profiles</label>
                <input className="form-input" name="codingProfiles" value={form.codingProfiles}
                  onChange={handleChange} placeholder="e.g. LeetCode: anitha | HackerRank: anitha_r" />
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Skills Summary</label>
                <textarea className="form-input" name="skills" rows="2" value={form.skills}
                  onChange={handleChange} placeholder="e.g. Java, Spring Boot, React, MySQL" />
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Achievements</label>
                <textarea className="form-input" name="achievements" rows="3" value={form.achievements}
                  onChange={handleChange} placeholder="Awards, hackathons, paper presentations..." />
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : '✔ Save Profile'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ProfileForm;
