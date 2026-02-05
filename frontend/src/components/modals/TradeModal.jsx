import React, { useState, useEffect } from 'react';
import { X, TrendingUp, TrendingDown, AlertCircle } from 'lucide-react';

const TradeModal = ({ isOpen, onClose, stock, type = 'buy', onSubmit, walletBalance }) => {
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen) {
      setQuantity(1);
      setError('');
    }
  }, [isOpen, stock]);

  if (!isOpen || !stock) return null;

  const totalCost = stock.currentPrice * quantity;
  const isBuy = type === 'buy';
  
  const canAfford = isBuy ? walletBalance >= totalCost : true;
  const hasEnoughShares = !isBuy ? (stock.quantity || 0) >= quantity : true;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (quantity <= 0) {
      setError('Quantity must be greater than 0');
      return;
    }

    if (isBuy && !canAfford) {
      setError('Insufficient wallet balance');
      return;
    }

    if (!isBuy && !hasEnoughShares) {
      setError('Not enough shares to sell');
      return;
    }

    setLoading(true);
    try {
      await onSubmit({
        ticker: stock.ticker,
        quantity: quantity,
        type: type
      });
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Transaction failed');
    } finally {
      setLoading(false);
    }
  };

  const handleMaxClick = () => {
    if (isBuy) {
      const maxShares = Math.floor(walletBalance / stock.currentPrice);
      setQuantity(Math.max(1, maxShares));
    } else {
      setQuantity(stock.quantity || 0);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>
            {isBuy ? 'Buy' : 'Sell'} {stock.ticker}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {/* Stock Info */}
            <div className="stock-preview">
              <div className="stock-preview-row">
                <span className="stock-preview-label">Stock</span>
                <span className="stock-preview-value">{stock.companyName}</span>
              </div>
              <div className="stock-preview-row">
                <span className="stock-preview-label">Current Price</span>
                <span className="stock-preview-value">${stock.currentPrice?.toFixed(2)}</span>
              </div>
              <div className="stock-preview-row">
                <span className="stock-preview-label">Day Change</span>
                <span className={`stock-preview-value price-change ${stock.dayChangePercent >= 0 ? 'positive' : 'negative'}`}>
                  {stock.dayChangePercent >= 0 ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                  {stock.dayChangePercent >= 0 ? '+' : ''}{stock.dayChangePercent?.toFixed(2)}%
                </span>
              </div>
              {isBuy && (
                <div className="stock-preview-row">
                  <span className="stock-preview-label">Available Balance</span>
                  <span className="stock-preview-value">${walletBalance?.toFixed(2)}</span>
                </div>
              )}
              {!isBuy && (
                <div className="stock-preview-row">
                  <span className="stock-preview-label">Shares Owned</span>
                  <span className="stock-preview-value">{stock.quantity || 0}</span>
                </div>
              )}
            </div>

            {/* Quantity Input */}
            <div className="form-group">
              <label className="form-label">Quantity</label>
              <div style={{ display: 'flex', gap: '8px' }}>
                <input
                  type="number"
                  className="form-input"
                  value={quantity}
                  onChange={(e) => setQuantity(parseInt(e.target.value) || 0)}
                  min="1"
                  max={!isBuy ? stock.quantity : undefined}
                />
                <button 
                  type="button" 
                  className="btn btn-outline"
                  onClick={handleMaxClick}
                >
                  Max
                </button>
              </div>
            </div>

            {/* Order Summary */}
            <div className="stock-preview" style={{ marginBottom: 0 }}>
              <div className="stock-preview-row">
                <span className="stock-preview-label">Order Type</span>
                <span className={`badge ${isBuy ? 'badge-buy' : 'badge-sell'}`}>
                  {isBuy ? 'BUY' : 'SELL'}
                </span>
              </div>
              <div className="stock-preview-row">
                <span className="stock-preview-label">Total {isBuy ? 'Cost' : 'Value'}</span>
                <span className="stock-preview-value" style={{ fontSize: '1.25rem' }}>
                  ${totalCost.toFixed(2)}
                </span>
              </div>
              {isBuy && (
                <div className="stock-preview-row">
                  <span className="stock-preview-label">Balance After</span>
                  <span className="stock-preview-value">
                    ${(walletBalance - totalCost).toFixed(2)}
                  </span>
                </div>
              )}
            </div>

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
              className={`btn ${isBuy ? 'btn-success' : 'btn-danger'}`}
              disabled={loading || (isBuy && !canAfford) || (!isBuy && !hasEnoughShares)}
            >
              {loading ? 'Processing...' : `${isBuy ? 'Buy' : 'Sell'} ${stock.ticker}`}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TradeModal;
