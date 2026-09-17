import React from 'react';

/**
 * Read-only, backend-computed eligibility report for one job.
 * `result` is the EligibilityResultDto returned by GET /api/eligibility/check/{jobId}
 * - criteria are rendered exactly as the server evaluated them, never faked.
 */
function EligibilityModal({ isOpen, onClose, result, loading, jobRole }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal modal-lg" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>🎯 Eligibility Report</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {loading ? (
            <div className="loading-container"><div className="spinner" /></div>
          ) : !result ? (
            <div className="empty-state" style={{ padding: '24px 0' }}>
              <div className="empty-state-icon">📭</div>
              <h3>No Result</h3>
              <p>Could not load the eligibility report for this job.</p>
            </div>
          ) : (
            <>
              <div className="eligibility-banner"
                style={{
                  borderColor: result.eligible ? 'rgba(16,185,129,0.45)' : 'rgba(239,68,68,0.45)',
                  background: result.eligible ? 'rgba(16,185,129,0.08)' : 'rgba(239,68,68,0.08)',
                }}>
                <span style={{ fontSize: 28 }}>{result.eligible ? '✅' : '🚫'}</span>
                <div>
                  <p style={{ fontWeight: 700, fontSize: 15 }}>
                    {result.eligible ? 'You are eligible' : 'Not eligible'} — {result.jobRole || jobRole}
                  </p>
                  <p style={{ fontSize: 13, color: 'var(--text-muted)' }}>{result.summary}</p>
                </div>
              </div>

              <p className="form-section-title">Criteria evaluated by the server</p>
              <div className="criteria-list">
                {(result.criteria || []).map((c, idx) => (
                  <div key={idx} className={`criteria-row ${c.passed ? 'pass' : 'fail'}`}>
                    <span className="criteria-icon">{c.passed ? '✔' : '✖'}</span>
                    <div className="criteria-main">
                      <span className="criteria-name">{c.criterionName}</span>
                      {c.details && <span className="criteria-details">{c.details}</span>}
                    </div>
                    <div className="criteria-values">
                      <span>Required: <strong>{c.requiredValue ?? '—'}</strong></span>
                      <span>Yours: <strong>{c.actualValue ?? '—'}</strong></span>
                    </div>
                  </div>
                ))}
              </div>

              <div className="criteria-skills">
                <div>
                  <p className="form-section-title">✔ Matched skills</p>
                  <div className="chip-row">
                    {(result.matchedSkills || []).length === 0
                      ? <span className="chip chip-muted">None</span>
                      : result.matchedSkills.map((s) => <span key={s} className="chip chip-success">{s}</span>)}
                  </div>
                </div>
                <div>
                  <p className="form-section-title">✖ Missing skills</p>
                  <div className="chip-row">
                    {(result.missingSkills || []).length === 0
                      ? <span className="chip chip-muted">None</span>
                      : result.missingSkills.map((s) => <span key={s} className="chip chip-danger">{s}</span>)}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}

export default EligibilityModal;
