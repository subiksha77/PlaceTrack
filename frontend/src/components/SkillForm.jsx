import React, { useState, useEffect } from 'react';

const CATEGORIES = ['PROGRAMMING', 'FRAMEWORK', 'DATABASE', 'TOOL', 'OTHER'];

const initialForm = { skillName: '', category: 'PROGRAMMING' };

function validate(form) {
  const errors = {};
  if (!form.skillName.trim()) errors.skillName = 'Skill name is required';
  else if (form.skillName.trim().length > 60) errors.skillName = 'Skill name must be at most 60 characters';
  if (!CATEGORIES.includes(form.category)) errors.category = 'Select a category';
  return errors;
}

function SkillForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        skillName: editData.skillName || '',
        category: editData.category || 'PROGRAMMING',
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
    onSubmit({ skillName: form.skillName.trim(), category: form.category });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal modal-sm" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Skill' : '➕ Add Skill'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label className="form-label">Skill Name <span className="required">*</span></label>
              <input className={`form-input ${errors.skillName ? 'error' : ''}`}
                name="skillName" value={form.skillName} onChange={handleChange}
                placeholder="e.g. Java, React, MySQL" autoFocus />
              {errors.skillName && <span className="form-error">⚠ {errors.skillName}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Category <span className="required">*</span></label>
              <select className={`form-input ${errors.category ? 'error' : ''}`}
                name="category" value={form.category} onChange={handleChange}>
                {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
              </select>
              {errors.category && <span className="form-error">⚠ {errors.category}</span>}
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Skill' : '✔ Add Skill'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default SkillForm;
