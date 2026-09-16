import React, { useEffect } from 'react';

function Toast({ toasts, removeToast }) {
  useEffect(() => {
    const timers = toasts.map((toast) => setTimeout(() => removeToast(toast.id), 3500));
    return () => timers.forEach((timer) => clearTimeout(timer));
  }, [toasts, removeToast]);

  const icons = { success: '✅', error: '❌', info: 'ℹ️' };

  return (
    <div className="toast-container">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast-${toast.type}`}>
          <span className="toast-icon">{icons[toast.type] || 'ℹ️'}</span>
          <span style={{ flex: 1 }}>{toast.message}</span>
          <button
            onClick={() => removeToast(toast.id)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'inherit', fontSize: 16, padding: '0 4px' }}
          >×</button>
        </div>
      ))}
    </div>
  );
}

export default Toast;
