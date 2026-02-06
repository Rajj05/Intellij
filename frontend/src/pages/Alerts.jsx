import React, { useState, useEffect, useCallback } from 'react';
import { Bell, Plus, Trash2, AlertTriangle, TrendingDown, TrendingUp } from 'lucide-react';
import Header from '../components/layout/Header';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { getUserAlerts, createAlert, getAllStocks } from '../api/stockApi';

const Alerts = ({ userId }) => {
  const [alerts, setAlerts] = useState([]);
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddAlert, setShowAddAlert] = useState(false);
  const [newAlert, setNewAlert] = useState({
    ticker: '',
    alertType: 'PRICE_DROP',
    threshold: 5
  });

  const fetchData = useCallback(async () => {
    try {
      const [alertsRes, stocksRes] = await Promise.all([
        getUserAlerts(userId).catch((err) => {
          console.error('Error fetching alerts:', err);
          return { success: true, data: [] };
        }),
        getAllStocks()
      ]);
      
      console.log('Alerts response:', alertsRes);
      console.log('Alerts data:', alertsRes.data);
      
      if (alertsRes.success) {
        const alertsData = alertsRes.data || [];
        console.log('Setting alerts to:', alertsData);
        setAlerts(alertsData);
      }
      if (stocksRes.success) {
        setStocks(stocksRes.data);
      }
    } catch (err) {
      console.error('Failed to fetch data:', err);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleCreateAlert = async (e) => {
    e.preventDefault();
    try {
      console.log('Creating alert...');
      const result = await createAlert({
        userId,
        ticker: newAlert.ticker,
        alertType: newAlert.alertType,
        threshold: parseFloat(newAlert.threshold)
      });
      console.log('Alert created:', result);
      setShowAddAlert(false);
      setNewAlert({ ticker: '', alertType: 'PRICE_DROP', threshold: 5 });
      await fetchData();
      console.log('Data refetched');
    } catch (err) {
      console.error('Failed to create alert:', err);
      alert('Failed to create alert: ' + (err.message || 'Unknown error'));
    }
  };
  
  const handleDeleteAlert = async (alertId) => {
    if (window.confirm('Are you sure you want to delete this alert?')) {
      try {
        await fetch(`http://localhost:8080/api/alerts/${alertId}`, {
          method: 'DELETE'
        });
        fetchData();
      } catch (err) {
        console.error('Failed to delete alert:', err);
      }
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading alerts..." />;
  }

  // Find underperforming stocks (down more than 3%)
  const underperformingStocks = stocks.filter(s => s.dayChangePercent < -3);

  return (
    <>
      <Header 
        title="Price Alerts" 
        subtitle="Get notified when stocks hit your target prices"
        userId={userId}
      />
      
      <div className="page-content">
        {/* Underperforming Stocks Warning */}
        {underperformingStocks.length > 0 && (
          <div className="alert alert-warning" style={{ marginBottom: '24px' }}>
            <AlertTriangle size={20} />
            <div>
              <strong>Market Alert:</strong> {underperformingStocks.length} stock(s) down more than 3% today:
              {' '}
              {underperformingStocks.map(s => (
                <span key={s.ticker} style={{ marginRight: '8px' }}>
                  <strong>{s.ticker}</strong> ({s.dayChangePercent.toFixed(2)}%)
                </span>
              ))}
            </div>
          </div>
        )}

        {/* Add Alert Button */}
        <div style={{ marginBottom: '24px' }}>
          <button 
            className="btn btn-primary"
            onClick={() => setShowAddAlert(!showAddAlert)}
          >
            <Plus size={18} />
            Create Alert
          </button>
        </div>

        {/* Add Alert Form */}
        {showAddAlert && (
          <div className="content-section" style={{ marginBottom: '24px' }}>
            <div className="section-header">
              <h3>Create New Alert</h3>
            </div>
            <form onSubmit={handleCreateAlert} style={{ padding: '20px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px' }}>
                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Stock</label>
                  <select
                    className="form-input form-select"
                    value={newAlert.ticker}
                    onChange={(e) => setNewAlert({ ...newAlert, ticker: e.target.value })}
                    required
                  >
                    <option value="">Select a stock</option>
                    {stocks.map((stock) => (
                      <option key={stock.ticker} value={stock.ticker}>
                        {stock.ticker} - {stock.companyName}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Alert Type</label>
                  <select
                    className="form-input form-select"
                    value={newAlert.alertType}
                    onChange={(e) => setNewAlert({ ...newAlert, alertType: e.target.value })}
                  >
                    <option value="PRICE_DROP">Price Drop</option>
                    <option value="PRICE_RISE">Price Rise</option>
                    <option value="DAILY_LOSS">Daily Loss %</option>
                    <option value="DAILY_GAIN">Daily Gain %</option>
                  </select>
                </div>

                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Threshold (%)</label>
                  <input
                    type="number"
                    className="form-input"
                    value={newAlert.threshold}
                    onChange={(e) => setNewAlert({ ...newAlert, threshold: parseFloat(e.target.value) })}
                    min="0.1"
                    step="0.1"
                    required
                  />
                </div>
              </div>

              <div style={{ marginTop: '20px', display: 'flex', gap: '12px' }}>
                <button type="submit" className="btn btn-success">
                  Create Alert
                </button>
                <button 
                  type="button" 
                  className="btn btn-outline"
                  onClick={() => setShowAddAlert(false)}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Active Alerts */}
        <div className="content-section">
          <div className="section-header">
            <h3>Your Alerts ({alerts.length})</h3>
          </div>
          
          {alerts.length > 0 ? (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Stock</th>
                  <th>Alert Type</th>
                  <th>Threshold (%)</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {alerts.map((alert) => (
                  <tr key={alert.id}>
                    <td>
                      <div className="stock-info">
                        <div className="stock-icon">
                          {alert.ticker?.slice(0, 2)}
                        </div>
                        <div className="stock-name">{alert.ticker}</div>
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${alert.alertType?.includes('DROP') || alert.alertType?.includes('LOSS') ? 'badge-sell' : 'badge-buy'}`}>
                        {alert.alertType?.includes('DROP') || alert.alertType?.includes('LOSS') 
                          ? <TrendingDown size={12} style={{ marginRight: '4px' }} />
                          : <TrendingUp size={12} style={{ marginRight: '4px' }} />
                        }
                        {alert.alertType?.replace('_', ' ')}
                      </span>
                    </td>
                    <td>{alert.threshold}%</td>
                    <td>
                      <button 
                        className="btn btn-outline btn-sm btn-icon"
                        onClick={() => handleDeleteAlert(alert.id)}
                        title="Delete alert"
                      >
                        <Trash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div className="empty-state">
              <Bell size={48} />
              <h4>No Alerts Set</h4>
              <p>Create alerts to get notified when stocks hit your target prices.</p>
            </div>
          )}
        </div>

        {/* Info Card */}
        <div className="content-section" style={{ marginTop: '24px' }}>
          <div style={{ padding: '20px' }}>
            <h4 style={{ marginBottom: '12px' }}>💡 Alert Types Explained</h4>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '16px', color: 'var(--text-secondary)' }}>
              <div>
                <strong style={{ color: 'var(--text-primary)' }}>Price Drop:</strong>
                <p style={{ fontSize: '0.875rem' }}>Get notified when a stock drops by X% from your purchase price.</p>
              </div>
              <div>
                <strong style={{ color: 'var(--text-primary)' }}>Price Rise:</strong>
                <p style={{ fontSize: '0.875rem' }}>Get notified when a stock rises by X% from your purchase price.</p>
              </div>
              <div>
                <strong style={{ color: 'var(--text-primary)' }}>Daily Loss:</strong>
                <p style={{ fontSize: '0.875rem' }}>Get notified when a stock drops by X% in a single day.</p>
              </div>
              <div>
                <strong style={{ color: 'var(--text-primary)' }}>Daily Gain:</strong>
                <p style={{ fontSize: '0.875rem' }}>Get notified when a stock gains X% in a single day.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Alerts;