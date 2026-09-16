import React from 'react';

/**
 * Read-only detail modal used to "view one" record (student / company / placement).
 * `items` is an array of { label, value, type } where type is one of:
 *   'text' (default), 'badge', 'mono', 'date'
 */
function DetailModal({ isOpen, onClose, icon, title, subtitle, items = [] }) {
  if (!isOpen) return null;

  const renderValue = (item) => {
    if (item.type === 'badge') {
      return <span className="badge">{item.value}</span>;
    }
    if (item.type === 'mono') {
      return <span style={{ fontFamily: 'monospace', fontSize: 13, color: 'var(--text-secondary)' }}>{item.value}</span>;
    }
    if (item.type === 'date') {
      return <span style={{ color: 'var(--text-secondary)' }}>📅 {item.value}</span>;
    }
    return item.value || <span style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>—</span>;
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{icon} {title}</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {subtitle && <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 18 }}>{subtitle}</p>}
          <div className="detail-list">
            {items.map((item, idx) => (
              <div key={idx} className="detail-row">
                <span className="detail-label">{item.label}</span>
                <span className="detail-value">{renderValue(item)}</span>
              </div>
            ))}
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}

export default DetailModal;
