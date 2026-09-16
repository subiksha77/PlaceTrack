import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

const DEPARTMENTS = ['CSE', 'ECE', 'EEE', 'MECH', 'CIVIL', 'IT', 'AIDS', 'AIML', 'MBA', 'MCA'];

const initialForm = {
  fullName: '', email: '', password: '', confirmPassword: '', department: '', year: '',
};

function Register({ onRegister }) {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
    if (serverError) setServerError('');
  };

  const validate = () => {
    const errs = {};
    if (!form.fullName.trim()) errs.fullName = 'Full name is required';
    else if (form.fullName.trim().length < 2) errs.fullName = 'Name must be at least 2 characters';

    if (!form.email.trim()) errs.email = 'Email is required';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errs.email = 'Enter a valid email';

    if (!form.password) errs.password = 'Password is required';
    else if (form.password.length < 8) errs.password = 'Password must be at least 8 characters';
    else if (!/(?=.*[A-Za-z])(?=.*\d)/.test(form.password)) errs.password = 'Password needs at least one letter and one number';

    if (!form.confirmPassword) errs.confirmPassword = 'Confirm your password';
    else if (form.confirmPassword !== form.password) errs.confirmPassword = 'Passwords do not match';

    if (!form.department) errs.department = 'Department is required';
    if (!form.year) errs.year = 'Year is required';

    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }

    setLoading(true);
    try {
      const res = await authService.register({
        fullName: form.fullName.trim(),
        email: form.email.trim(),
        password: form.password,
        department: form.department,
        year: Number(form.year),
      });
      // Auto-login: the backend returns the safe user profile
      authService.saveSession(res.data);
      onRegister(res.data);
      navigate('/');
    } catch (err) {
      const data = err.response?.data;
      if (data?.fieldErrors) {
        const mapped = {};
        if (data.fieldErrors.fullName) mapped.fullName = data.fieldErrors.fullName;
        if (data.fieldErrors.email) mapped.email = data.fieldErrors.email;
        if (data.fieldErrors.password) mapped.password = data.fieldErrors.password;
        if (data.fieldErrors.department) mapped.department = data.fieldErrors.department;
        if (data.fieldErrors.year) mapped.year = data.fieldErrors.year;
        setErrors(mapped);
      }
      setServerError(data?.message || err.displayMessage || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card auth-card-wide">
        <div className="auth-brand">
          <div className="auth-logo">🎯</div>
          <h1>Create your account</h1>
          <p>Join PlaceTrack and start tracking placements.</p>
        </div>

        {serverError && (
          <div className="auth-error-banner">⚠ {serverError}</div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-grid">
            <div className="form-group full-width">
              <label className="form-label">Full Name <span className="required">*</span></label>
              <input
                className={`form-input ${errors.fullName ? 'error' : ''}`}
                name="fullName" autoComplete="name"
                placeholder="Enter your full name"
                value={form.fullName} onChange={handleChange}
              />
              {errors.fullName && <span className="form-error">⚠ {errors.fullName}</span>}
            </div>

            <div className="form-group full-width">
              <label className="form-label">Email <span className="required">*</span></label>
              <input
                className={`form-input ${errors.email ? 'error' : ''}`}
                type="email" name="email" autoComplete="email"
                placeholder="you@college.edu"
                value={form.email} onChange={handleChange}
              />
              {errors.email && <span className="form-error">⚠ {errors.email}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Password <span className="required">*</span></label>
              <div className="password-wrapper">
                <input
                  className={`form-input ${errors.password ? 'error' : ''}`}
                  type={showPassword ? 'text' : 'password'} name="password" autoComplete="new-password"
                  placeholder="Min 8 chars, letter + number"
                  value={form.password} onChange={handleChange}
                />
                <button type="button" className="password-toggle"
                  onClick={() => setShowPassword((s) => !s)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}>
                  {showPassword ? '🙈' : '👁'}
                </button>
              </div>
              {errors.password && <span className="form-error">⚠ {errors.password}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Confirm Password <span className="required">*</span></label>
              <div className="password-wrapper">
                <input
                  className={`form-input ${errors.confirmPassword ? 'error' : ''}`}
                  type={showConfirm ? 'text' : 'password'} name="confirmPassword" autoComplete="new-password"
                  placeholder="Re-enter your password"
                  value={form.confirmPassword} onChange={handleChange}
                />
                <button type="button" className="password-toggle"
                  onClick={() => setShowConfirm((s) => !s)}
                  aria-label={showConfirm ? 'Hide password' : 'Show password'}>
                  {showConfirm ? '🙈' : '👁'}
                </button>
              </div>
              {errors.confirmPassword && <span className="form-error">⚠ {errors.confirmPassword}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Department <span className="required">*</span></label>
              <select
                className={`form-select ${errors.department ? 'error' : ''}`}
                name="department" value={form.department} onChange={handleChange}>
                <option value="">-- Select Department --</option>
                {DEPARTMENTS.map((d) => <option key={d} value={d}>{d}</option>)}
              </select>
              {errors.department && <span className="form-error">⚠ {errors.department}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Year <span className="required">*</span></label>
              <select
                className={`form-select ${errors.year ? 'error' : ''}`}
                name="year" value={form.year} onChange={handleChange}>
                <option value="">-- Select Year --</option>
                {[1, 2, 3, 4].map((y) => <option key={y} value={y}>Year {y}</option>)}
              </select>
              {errors.year && <span className="form-error">⚠ {errors.year}</span>}
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? '⏳ Creating account...' : '✔ Create Account'}
          </button>
        </form>

        <div className="auth-alt">
          Already have an account? <Link to="/login">Login</Link>
        </div>
      </div>
    </div>
  );
}

export default Register;
