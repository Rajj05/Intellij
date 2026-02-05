import React, { useState, useEffect } from 'react';
import { X, Check, AlertCircle } from 'lucide-react';

const AddFundsModal = ({ isOpen, onClose, currentBalance, onSubmit }) => {
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const quickAmounts = [1000, 5000, 10000, 25000];

  useEffect(() => {
    if (isOpen) {
      setAmount('');
      setError('');
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    const numAmount = parseFloat(amount);

    if (!numAmount || numAmount <= 0) {
      setError('Please enter a valid amount');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await onSubmit(numAmount);
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add funds');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Add Funds to Wallet</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {/* Current Balance */}
            <div className="stock-preview">
              <div className="stock-preview-row">
                <span className="stock-preview-label">Current Balance</span>
                <span className="stock-preview-value">${currentBalance?.toFixed(2)}</span>
              </div>
            </div>

            {/* Quick Amount Buttons */}
            <div className="form-group">
              <label className="form-label">Quick Add</label>
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                {quickAmounts.map((qa) => (
                  <button
                    key={qa}
                    type="button"
                    className={`btn btn-outline btn-sm ${parseFloat(amount) === qa ? 'btn-primary' : ''}`}
                    onClick={() => setAmount(qa.toString())}
                  >
                    ${qa.toLocaleString()}
                  </button>
                ))}
              </div>
            </div>

            {/* Amount Input */}
            <div className="form-group">
              <label className="form-label">Amount ($)</label>
              <input
                type="number"
                className="form-input"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="Enter amount"
                min="1"
                step="0.01"
              />
            </div>

            {/* New Balance Preview */}
            {amount && parseFloat(amount) > 0 && (
              <div className="stock-preview" style={{ marginBottom: 0 }}>
                <div className="stock-preview-row">
                  <span className="stock-preview-label">New Balance</span>
                  <span className="stock-preview-value" style={{ color: 'var(--profit-green)' }}>
                    ${(currentBalance + parseFloat(amount)).toFixed(2)}
                  </span>
                </div>
              </div>
            )}

            {/* Error Message */}
            {error && (
              <div className="alert alert-error" style={{ marginTop: '16px' }}>
                <AlertCircle size={18} />
                <span>{error}</span>
              </div>
            )}
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-success"
              disabled={loading || !amount || parseFloat(amount) <= 0}
            >
              {loading ? 'Processing...' : 'Add Funds'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddFundsModal;
