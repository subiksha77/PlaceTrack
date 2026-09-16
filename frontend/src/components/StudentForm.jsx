import React, { useState, useEffect } from 'react';

const DEPARTMENTS = ['CSE', 'ECE', 'EEE', 'MECH', 'CIVIL', 'IT', 'AIDS', 'AIML', 'MBA', 'MCA'];

const initialForm = {
  studentName: '', email: '', department: '', year: '', cgpa: '',
  phone: '', skills: '', placementStatus: 'NOT_PLACED',
};

function validate(form) {
  const errors = {};
  if (!form.studentName.trim()) errors.studentName = 'Name is required';
  else if (form.studentName.trim().length < 2) errors.studentName = 'Name must be at least 2 characters';

  if (!form.email.trim()) errors.email = 'Email is required';
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errors.email = 'Enter a valid email';

  if (!form.department) errors.department = 'Department is required';

  if (!form.year) errors.year = 'Year is required';
  else if (Number(form.year) < 1 || Number(form.year) > 4) errors.year = 'Year must be 1–4';

  if (form.cgpa === '') errors.cgpa = 'CGPA is required';
  else if (Number(form.cgpa) < 0 || Number(form.cgpa) > 10) errors.cgpa = 'CGPA must be 0–10';

  if (!form.phone.trim()) errors.phone = 'Phone is required';
  else if (!/^[6-9]\d{9}$/.test(form.phone)) errors.phone = 'Enter a valid 10-digit mobile number';

  return errors;
}

function StudentForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        studentName: editData.studentName || '',
        email: editData.email || '',
        department: editData.department || '',
        year: editData.year?.toString() || '',
        cgpa: editData.cgpa?.toString() || '',
        phone: editData.phone || '',
        skills: editData.skills || '',
        placementStatus: editData.placementStatus || 'NOT_PLACED',
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
      year: Number(form.year),
      cgpa: Number(form.cgpa),
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Student' : '➕ Add New Student'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Student Name <span className="required">*</span></label>
                <input className={`form-input ${errors.studentName ? 'error' : ''}`}
                  name="studentName" value={form.studentName} onChange={handleChange}
                  placeholder="e.g. Priya Sharma" />
                {errors.studentName && <span className="form-error">⚠ {errors.studentName}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Email <span className="required">*</span></label>
                <input className={`form-input ${errors.email ? 'error' : ''}`}
                  name="email" type="email" value={form.email} onChange={handleChange}
                  placeholder="e.g. priya@college.edu" />
                {errors.email && <span className="form-error">⚠ {errors.email}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Department <span className="required">*</span></label>
                <select className={`form-select ${errors.department ? 'error' : ''}`}
                  name="department" value={form.department} onChange={handleChange}>
                  <option value="">-- Select Department --</option>
                  {DEPARTMENTS.map((d) => <option key={d} value={d}>{d}</option>)}
                </select>
                {errors.department && <span className="form-error">⚠ {errors.department}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Year <span className="required">*</span></label>
                <select className={`form-select ${errors.year ? 'error' : ''}`}
                  name="year" value={form.year} onChange={handleChange}>
                  <option value="">-- Select Year --</option>
                  {[1,2,3,4].map((y) => <option key={y} value={y}>Year {y}</option>)}
                </select>
                {errors.year && <span className="form-error">⚠ {errors.year}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">CGPA <span className="required">*</span></label>
                <input className={`form-input ${errors.cgpa ? 'error' : ''}`}
                  name="cgpa" type="number" step="0.01" min="0" max="10"
                  value={form.cgpa} onChange={handleChange} placeholder="e.g. 8.5" />
                {errors.cgpa && <span className="form-error">⚠ {errors.cgpa}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Phone <span className="required">*</span></label>
                <input className={`form-input ${errors.phone ? 'error' : ''}`}
                  name="phone" value={form.phone} onChange={handleChange}
                  placeholder="e.g. 9876543210" maxLength={10} />
                {errors.phone && <span className="form-error">⚠ {errors.phone}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Placement Status</label>
                <select className="form-select" name="placementStatus" value={form.placementStatus} onChange={handleChange}>
                  <option value="NOT_PLACED">Not Placed</option>
                  <option value="PLACED">Placed</option>
                </select>
              </div>

              <div className="form-group full-width">
                <label className="form-label">Skills</label>
                <textarea className="form-textarea" name="skills" value={form.skills}
                  onChange={handleChange} placeholder="e.g. Java, React, MySQL, Spring Boot" />
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Student' : '✔ Add Student'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default StudentForm;
