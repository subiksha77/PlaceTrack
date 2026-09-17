import React, { useState, useEffect } from 'react';

const initialForm = {
  projectName: '', description: '', technologies: '', projectLink: '', projectRole: '',
};

const blankToNull = (v) => {
  const trimmed = typeof v === 'string' ? v.trim() : v;
  return trimmed === '' || trimmed === undefined || trimmed === null ? null : trimmed;
};

function validate(form) {
  const errors = {};
  if (!form.projectName.trim()) errors.projectName = 'Project name is required';
  else if (form.projectName.trim().length > 120) errors.projectName = 'Project name must be at most 120 characters';

  if (form.description.length > 2000) errors.description = 'Description must be at most 2000 characters';
  if (form.technologies.length > 300) errors.technologies = 'Technologies must be at most 300 characters';
  if (form.projectLink.trim() && form.projectLink.trim().length > 300)
    errors.projectLink = 'Project link must be at most 300 characters';
  if (form.projectRole.length > 60) errors.projectRole = 'Project role must be at most 60 characters';

  return errors;
}

function ProjectForm({ isOpen, onClose, onSubmit, editData, loading }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editData) {
      setForm({
        projectName: editData.projectName || '',
        description: editData.description || '',
        technologies: editData.technologies || '',
        projectLink: editData.projectLink || '',
        projectRole: editData.projectRole || '',
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
      projectName: form.projectName.trim(),
      description: blankToNull(form.description),
      technologies: blankToNull(form.technologies),
      projectLink: blankToNull(form.projectLink),
      projectRole: blankToNull(form.projectRole),
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editData ? '✏️ Edit Project' : '➕ Add Project'}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group form-group-wide">
                <label className="form-label">Project Name <span className="required">*</span></label>
                <input className={`form-input ${errors.projectName ? 'error' : ''}`}
                  name="projectName" value={form.projectName} onChange={handleChange}
                  placeholder="e.g. PlaceTrack - Placement Management System" />
                {errors.projectName && <span className="form-error">⚠ {errors.projectName}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Your Role</label>
                <input className={`form-input ${errors.projectRole ? 'error' : ''}`}
                  name="projectRole" value={form.projectRole} onChange={handleChange}
                  placeholder="e.g. Full Stack Developer" />
                {errors.projectRole && <span className="form-error">⚠ {errors.projectRole}</span>}
              </div>

              <div className="form-group">
                <label className="form-label">Technologies</label>
                <input className={`form-input ${errors.technologies ? 'error' : ''}`}
                  name="technologies" value={form.technologies} onChange={handleChange}
                  placeholder="e.g. React, Spring Boot, MySQL" />
                {errors.technologies && <span className="form-error">⚠ {errors.technologies}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Project Link</label>
                <input className={`form-input ${errors.projectLink ? 'error' : ''}`}
                  name="projectLink" value={form.projectLink} onChange={handleChange}
                  placeholder="https://github.com/username/project" />
                {errors.projectLink && <span className="form-error">⚠ {errors.projectLink}</span>}
              </div>

              <div className="form-group form-group-wide">
                <label className="form-label">Description</label>
                <textarea className={`form-input ${errors.description ? 'error' : ''}`}
                  name="description" rows="3" value={form.description} onChange={handleChange}
                  placeholder="What the project does, your contribution and the outcome..." />
                {errors.description && <span className="form-error">⚠ {errors.description}</span>}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '⏳ Saving...' : editData ? '✔ Update Project' : '✔ Add Project'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ProjectForm;
