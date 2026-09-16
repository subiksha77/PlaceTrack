import React from 'react';

function ConfirmDialog({ isOpen, title, message, onConfirm, onCancel, confirmLabel = 'Delete', confirmClass = 'btn-danger' }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal modal-sm" onClick={(e) => e.stopPropagation()}>
        <div className="modal-body" style={{ padding: '28px 28px 20px' }}>
          <div className="confirm-icon">⚠️</div>
          <h3 style={{ textAlign: 'center', marginBottom: 10, fontSize: 18, fontWeight: 700 }}>{title}</h3>
          <p className="confirm-text">{message}</p>
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onCancel}>Cancel</button>
          <button className={`btn ${confirmClass}`} onClick={onConfirm}>{confirmLabel}</button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmDialog;
