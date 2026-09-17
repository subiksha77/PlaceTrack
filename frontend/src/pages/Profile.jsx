import React, { useState, useEffect, useCallback } from 'react';
import ProfileForm from '../components/ProfileForm';
import SkillForm from '../components/SkillForm';
import ProjectForm from '../components/ProjectForm';
import CertificationForm from '../components/CertificationForm';
import InternshipForm from '../components/InternshipForm';
import ConfirmDialog from '../components/ConfirmDialog';
import { profileService } from '../services/profileService';

const CATEGORY_ICONS = {
  PROGRAMMING: '💻', FRAMEWORK: '🧩', DATABASE: '🗄️', TOOL: '🛠️', OTHER: '📌',
};

function Profile({ showToast }) {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // One modal per repeatable section (skills / projects / certifications / internships)
  const [profileFormOpen, setProfileFormOpen] = useState(false);
  const [skillForm, setSkillForm] = useState({ open: false, data: null });
  const [projectForm, setProjectForm] = useState({ open: false, data: null });
  const [certForm, setCertForm] = useState({ open: false, data: null });
  const [internshipForm, setInternshipForm] = useState({ open: false, data: null });
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchProfile = useCallback(() => {
    setLoading(true);
    profileService.getProfile()
      .then((res) => setProfile(res.data))
      .catch((err) => showToast(err.displayMessage || 'Failed to load your profile', 'error'))
      .finally(() => setLoading(false));
  }, [showToast]);

  useEffect(() => { fetchProfile(); }, [fetchProfile]);

  // Every mutation returns the full recomputed ProfileResponse, so we simply
  // replace the page state with the server's answer (no local re-derivation).
  const runMutation = async (promise, successMessage) => {
    setSaving(true);
    try {
      const res = await promise;
      setProfile(res.data);
      showToast(successMessage, 'success');
      return true;
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Operation failed', 'error');
      return false;
    } finally {
      setSaving(false);
    }
  };

  // ===== Profile basics =====
  const handleProfileSubmit = async (data) => {
    const ok = await runMutation(profileService.updateProfile(data), 'Profile updated successfully!');
    if (ok) setProfileFormOpen(false);
  };

  // ===== Skills =====
  const handleSkillSubmit = async (data) => {
    const ok = skillForm.data
      ? await runMutation(profileService.updateSkill(skillForm.data.id, data), 'Skill updated!')
      : await runMutation(profileService.addSkill(data), 'Skill added!');
    if (ok) setSkillForm({ open: false, data: null });
  };

  // ===== Projects =====
  const handleProjectSubmit = async (data) => {
    const ok = projectForm.data
      ? await runMutation(profileService.updateProject(projectForm.data.id, data), 'Project updated!')
      : await runMutation(profileService.addProject(data), 'Project added!');
    if (ok) setProjectForm({ open: false, data: null });
  };

  // ===== Certifications =====
  const handleCertSubmit = async (data) => {
    const ok = certForm.data
      ? await runMutation(profileService.updateCertification(certForm.data.id, data), 'Certification updated!')
      : await runMutation(profileService.addCertification(data), 'Certification added!');
    if (ok) setCertForm({ open: false, data: null });
  };

  // ===== Internships =====
  const handleInternshipSubmit = async (data) => {
    const ok = internshipForm.data
      ? await runMutation(profileService.updateInternship(internshipForm.data.id, data), 'Internship updated!')
      : await runMutation(profileService.addInternship(data), 'Internship added!');
    if (ok) setInternshipForm({ open: false, data: null });
  };

  // ===== Deletes (skills / projects / certifications / internships / resume) =====
  const handleDelete = async () => {
    const { kind, id } = deleteTarget;
    const calls = {
      skill: () => profileService.deleteSkill(id),
      project: () => profileService.deleteProject(id),
      certification: () => profileService.deleteCertification(id),
      internship: () => profileService.deleteInternship(id),
      resume: () => profileService.deleteResume(),
    };
    const labels = {
      skill: 'Skill deleted!', project: 'Project deleted!', certification: 'Certification deleted!',
      internship: 'Internship deleted!', resume: 'Resume removed!',
    };
    await runMutation(calls[kind](), labels[kind]);
    setDeleteTarget(null);
  };

  // ===== Resume upload / download =====
  const handleResumeUpload = async (e) => {
    const file = e.target.files?.[0];
    e.target.value = '';
    if (!file) return;
    setSaving(true);
    try {
      const res = await profileService.uploadResume(file);
      setProfile(res.data);
      showToast('Resume uploaded successfully!', 'success');
    } catch (err) {
      showToast(err.response?.data?.message || err.displayMessage || 'Failed to upload resume', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleResumeDownload = async () => {
    try {
      const res = await profileService.downloadResume();
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', profile?.resume?.fileName || 'resume.pdf');
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      showToast(err.displayMessage || 'Failed to download resume', 'error');
    }
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner" /></div>;
  }

  if (!profile) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">👤</div>
        <h3>Profile unavailable</h3>
        <p>We could not load your placement profile. Please try again.</p>
        <button className="btn btn-primary" onClick={fetchProfile}>🔄 Retry</button>
      </div>
    );
  }

  const completeness = profile.completenessPercent ?? 0;
  const checklist = profile.completeness || [];
  const personalRows = [
    { label: 'Full Name', value: profile.studentName },
    { label: 'Email', value: profile.email },
    { label: 'Phone', value: profile.phone },
    { label: 'Date of Birth', value: profile.dateOfBirth },
    { label: 'Gender', value: profile.gender },
    { label: 'Location', value: profile.location },
    { label: 'City', value: profile.city },
    { label: 'State', value: profile.state },
    { label: 'Address', value: profile.address, wide: true },
  ];
  const academicRows = [
    { label: 'Register Number', value: profile.registerNumber },
    { label: 'Department', value: profile.department },
    { label: 'Degree', value: profile.degree },
    { label: 'Graduation Year', value: profile.graduationYear },
    { label: 'CGPA', value: profile.cgpa },
    { label: '10th %', value: profile.tenthPercentage },
    { label: '12th %', value: profile.twelfthPercentage },
    { label: 'Backlogs', value: profile.backlogs },
    { label: 'Placement Status', value: profile.placementStatus },
  ];

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h2>👤 My Placement Profile</h2>
          <p>Keep this complete — eligibility checks and job matches are computed from it</p>
        </div>
        <div className="page-header-right">
          <button className="btn btn-primary" onClick={() => setProfileFormOpen(true)} disabled={saving}>
            ✏️ Edit Profile
          </button>
        </div>
      </div>

      <div className="page-content">
        {/* ===== Completeness (computed by the backend) ===== */}
        <div className="card profile-completeness">
          <div className="completeness-ring" style={{ '--pct': `${completeness}%` }}>
            <div className="completeness-ring-inner">
              <span className="completeness-value">{completeness}%</span>
              <span className="completeness-caption">complete</span>
            </div>
          </div>
          <div className="completeness-body">
            <h3>Profile Completeness</h3>
            <div className="progress-track">
              <div className="progress-fill" style={{ width: `${completeness}%` }} />
            </div>
            <p className="completeness-note">
              {completeness === 100
                ? 'Your profile is complete — you are ready for eligibility checks.'
                : 'Complete the highlighted items below to improve your eligibility and job matches.'}
            </p>
            <div className="checklist-grid">
              {checklist.map((item, idx) => (
                <div key={idx} className={`checklist-item ${item.done ? 'done' : 'pending'}`}>
                  <span className="checklist-icon">{item.done ? '✔' : '○'}</span>
                  <span className="checklist-label">{item.label}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* ===== Personal & academic details ===== */}
        <div className="info-grid">
          <div className="card">
            <div className="section-head">
              <h3 className="section-title">🧾 Personal Details</h3>
            </div>
            <div className="info-list">
              {personalRows.map((row) => (
                <div key={row.label} className={`info-row ${row.wide ? 'wide' : ''}`}>
                  <span className="info-label">{row.label}</span>
                  <span className="info-value">{row.value ?? '—'}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="card">
            <div className="section-head">
              <h3 className="section-title">🎓 Academic Details</h3>
            </div>
            <div className="info-list">
              {academicRows.map((row) => (
                <div key={row.label} className="info-row">
                  <span className="info-label">{row.label}</span>
                  <span className="info-value">{row.value ?? '—'}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* ===== Professional links + resume ===== */}
        <div className="info-grid">
          <div className="card">
            <div className="section-head">
              <h3 className="section-title">🔗 Professional Profiles</h3>
            </div>
            <div className="info-list">
              <div className="info-row">
                <span className="info-label">LinkedIn</span>
                <span className="info-value">
                  {profile.linkedIn
                    ? <a href={profile.linkedIn} target="_blank" rel="noreferrer" className="link-text">{profile.linkedIn}</a>
                    : '—'}
                </span>
              </div>
              <div className="info-row">
                <span className="info-label">GitHub</span>
                <span className="info-value">
                  {profile.gitHub
                    ? <a href={profile.gitHub} target="_blank" rel="noreferrer" className="link-text">{profile.gitHub}</a>
                    : '—'}
                </span>
              </div>
              <div className="info-row wide">
                <span className="info-label">Coding Profiles</span>
                <span className="info-value">{profile.codingProfiles ?? '—'}</span>
              </div>
              <div className="info-row wide">
                <span className="info-label">Skills Summary</span>
                <span className="info-value">{profile.skills ?? '—'}</span>
              </div>
              <div className="info-row wide">
                <span className="info-label">Achievements</span>
                <span className="info-value">{profile.achievements ?? '—'}</span>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="section-head">
              <h3 className="section-title">📄 Resume</h3>
            </div>
            <div className="resume-box">
              {profile.resume?.hasResume ? (
                <>
                  <div className="resume-icon">📑</div>
                  <p className="resume-name">{profile.resume.fileName}</p>
                  {profile.resume.updatedAt && (
                    <p className="resume-meta">Uploaded {profile.resume.updatedAt}</p>
                  )}
                  <div className="resume-actions">
                    <button className="btn btn-primary btn-sm" onClick={handleResumeDownload} disabled={saving}>
                      ⬇️ Download
                    </button>
                    <button className="btn btn-secondary btn-sm" onClick={() => setDeleteTarget({ kind: 'resume', label: profile.resume.fileName })} disabled={saving}>
                      🗑️ Remove
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div className="resume-icon">📄</div>
                  <p className="resume-name">No resume uploaded yet</p>
                  <p className="resume-meta">PDF / DOC files are linked to every application you submit.</p>
                </>
              )}
              <label className={`btn btn-secondary btn-sm upload-label ${saving ? 'disabled' : ''}`}>
                ⬆️ {profile.resume?.hasResume ? 'Replace Resume' : 'Upload Resume'}
                <input type="file" accept=".pdf,.doc,.docx" onChange={handleResumeUpload} disabled={saving} hidden />
              </label>
            </div>
          </div>
        </div>
        {/* ===== Skills ===== */}
        <div className="card">
          <div className="section-head">
            <h3 className="section-title">
              🧠 Skills <span className="count-pill">{profile.skillList?.length || 0}</span>
            </h3>
            <button className="btn btn-secondary btn-sm" onClick={() => setSkillForm({ open: true, data: null })}>
              ＋ Add Skill
            </button>
          </div>
          {profile.skillList?.length ? (
            <div className="chip-row">
              {profile.skillList.map((s) => (
                <span key={s.id} className="chip">
                  <span className="chip-icon">{CATEGORY_ICONS[s.category] || '📌'}</span>
                  <span className="chip-text">{s.skillName}</span>
                  <span className="chip-cat">{s.category}</span>
                  <button className="chip-btn" title="Edit skill" onClick={() => setSkillForm({ open: true, data: s })}>✏️</button>
                  <button className="chip-btn danger" title="Delete skill" onClick={() => setDeleteTarget({ kind: 'skill', id: s.id, label: s.skillName })}>✕</button>
                </span>
              ))}
            </div>
          ) : (
            <p className="section-empty">No skills added yet — skills drive your job-match percentage.</p>
          )}
        </div>

        {/* ===== Projects ===== */}
        <div className="card">
          <div className="section-head">
            <h3 className="section-title">
              🚀 Projects <span className="count-pill">{profile.projects?.length || 0}</span>
            </h3>
            <button className="btn btn-secondary btn-sm" onClick={() => setProjectForm({ open: true, data: null })}>
              ＋ Add Project
            </button>
          </div>
          {profile.projects?.length ? (
            <div className="item-grid">
              {profile.projects.map((p) => (
                <div key={p.id} className="item-card">
                  <div className="item-card-head">
                    <span className="item-card-title">{p.projectName}</span>
                    <span className="item-actions">
                      <button className="btn btn-secondary btn-sm" title="Edit" onClick={() => setProjectForm({ open: true, data: p })}>✏️</button>
                      <button className="btn btn-danger btn-sm" title="Delete" onClick={() => setDeleteTarget({ kind: 'project', id: p.id, label: p.projectName })}>🗑️</button>
                    </span>
                  </div>
                  {p.projectRole && <span className="item-tag">Role: {p.projectRole}</span>}
                  {p.description && <p className="item-text">{p.description}</p>}
                  {p.technologies && <p className="item-meta">🛠️ {p.technologies}</p>}
                  {p.projectLink && (
                    <a className="link-text" href={p.projectLink} target="_blank" rel="noreferrer">🔗 {p.projectLink}</a>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <p className="section-empty">No projects added yet.</p>
          )}
        </div>
        {/* ===== Certifications ===== */}
        <div className="card">
          <div className="section-head">
            <h3 className="section-title">
              📜 Certifications <span className="count-pill">{profile.certifications?.length || 0}</span>
            </h3>
            <button className="btn btn-secondary btn-sm" onClick={() => setCertForm({ open: true, data: null })}>
              ＋ Add Certification
            </button>
          </div>
          {profile.certifications?.length ? (
            <div className="item-grid">
              {profile.certifications.map((c) => (
                <div key={c.id} className="item-card">
                  <div className="item-card-head">
                    <span className="item-card-title">{c.certificationName}</span>
                    <span className="item-actions">
                      <button className="btn btn-secondary btn-sm" title="Edit" onClick={() => setCertForm({ open: true, data: c })}>✏️</button>
                      <button className="btn btn-danger btn-sm" title="Delete" onClick={() => setDeleteTarget({ kind: 'certification', id: c.id, label: c.certificationName })}>🗑️</button>
                    </span>
                  </div>
                  <p className="item-meta">🏛️ {c.issuingOrganization}</p>
                  {c.issueDate && <p className="item-meta">📅 Issued {c.issueDate}</p>}
                  {c.credentialLink && (
                    <a className="link-text" href={c.credentialLink} target="_blank" rel="noreferrer">🔗 Credential</a>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <p className="section-empty">No certifications added yet.</p>
          )}
        </div>

        {/* ===== Internships ===== */}
        <div className="card">
          <div className="section-head">
            <h3 className="section-title">
              💼 Internships <span className="count-pill">{profile.internships?.length || 0}</span>
            </h3>
            <button className="btn btn-secondary btn-sm" onClick={() => setInternshipForm({ open: true, data: null })}>
              ＋ Add Internship
            </button>
          </div>
          {profile.internships?.length ? (
            <div className="item-grid">
              {profile.internships.map((i) => (
                <div key={i.id} className="item-card">
                  <div className="item-card-head">
                    <span className="item-card-title">{i.role}</span>
                    <span className="item-actions">
                      <button className="btn btn-secondary btn-sm" title="Edit" onClick={() => setInternshipForm({ open: true, data: i })}>✏️</button>
                      <button className="btn btn-danger btn-sm" title="Delete" onClick={() => setDeleteTarget({ kind: 'internship', id: i.id, label: `${i.role} @ ${i.companyName}` })}>🗑️</button>
                    </span>
                  </div>
                  <p className="item-meta">🏢 {i.companyName}</p>
                  <p className="item-meta">⏳ {i.duration}</p>
                  {i.description && <p className="item-text">{i.description}</p>}
                </div>
              ))}
            </div>
          ) : (
            <p className="section-empty">No internships added yet.</p>
          )}
        </div>
      </div>

      {/* ===== Modals ===== */}
      <ProfileForm
        isOpen={profileFormOpen}
        onClose={() => setProfileFormOpen(false)}
        onSubmit={handleProfileSubmit}
        editData={profile}
        loading={saving}
      />

      <SkillForm
        isOpen={skillForm.open}
        onClose={() => setSkillForm({ open: false, data: null })}
        onSubmit={handleSkillSubmit}
        editData={skillForm.data}
        loading={saving}
      />

      <ProjectForm
        isOpen={projectForm.open}
        onClose={() => setProjectForm({ open: false, data: null })}
        onSubmit={handleProjectSubmit}
        editData={projectForm.data}
        loading={saving}
      />

      <CertificationForm
        isOpen={certForm.open}
        onClose={() => setCertForm({ open: false, data: null })}
        onSubmit={handleCertSubmit}
        editData={certForm.data}
        loading={saving}
      />

      <InternshipForm
        isOpen={internshipForm.open}
        onClose={() => setInternshipForm({ open: false, data: null })}
        onSubmit={handleInternshipSubmit}
        editData={internshipForm.data}
        loading={saving}
      />

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Confirm Removal"
        message={deleteTarget?.kind === 'resume'
          ? <>Are you sure you want to remove <strong>{deleteTarget?.label}</strong> from your profile?</>
          : <>Are you sure you want to delete <strong>{deleteTarget?.label}</strong>? This action cannot be undone.</>}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default Profile;
