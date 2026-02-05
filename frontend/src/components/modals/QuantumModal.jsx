import React, { useState } from 'react';
import { X, Atom, TrendingUp, Shield, Rocket, Scale, Loader2, AlertCircle, RefreshCw } from 'lucide-react';
import './QuantumModal.css';

const QuantumModal = ({ isOpen, onClose }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [recommendations, setRecommendations] = useState(null);
  const [selectedProfile, setSelectedProfile] = useState('moderate');

  const riskProfiles = [
    {
      id: 'conservative',
      name: 'Conservative',
      description: 'Lower risk, stable returns',
      icon: Shield,
      color: '#10b981'
    },
    {
      id: 'moderate',
      name: 'Moderate',
      description: 'Balanced risk-reward',
      icon: Scale,
      color: '#6366f1'
    },
    {
      id: 'aggressive',
      name: 'Aggressive',
      description: 'Higher risk, growth focused',
      icon: Rocket,
      color: '#f59e0b'
    }
  ];

  const fetchRecommendations = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch(
        `http://localhost:5000/api/quantum/recommend?risk_profile=${selectedProfile}&num_stocks=5`
      );
      
      if (!response.ok) {
        throw new Error('Failed to fetch recommendations');
      }
      
      const data = await response.json();
      
      if (data.success) {
        setRecommendations(data);
      } else {
        throw new Error(data.error || 'Unknown error');
      }
    } catch (err) {
      setError(err.message || 'Failed to connect to Quantum API. Make sure the server is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setRecommendations(null);
    setError(null);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="quantum-modal-overlay" onClick={handleClose}>
      <div className="quantum-modal" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="quantum-modal-header">
          <div className="quantum-title">
            <Atom className="quantum-icon spinning" size={28} />
            <div>
              <h2>Quantum Portfolio Optimizer</h2>
              <p>AI-powered recommendations using quantum computing</p>
            </div>
          </div>
          <button className="close-btn" onClick={handleClose}>
            <X size={20} />
          </button>
        </div>

        {/* Content */}
        <div className="quantum-modal-content">
          {!recommendations ? (
            <>
              {/* Risk Profile Selection */}
              <div className="risk-profile-section">
                <h3>Select Your Risk Profile</h3>
                <div className="risk-profiles">
                  {riskProfiles.map((profile) => {
                    const IconComponent = profile.icon;
                    return (
                      <button
                        key={profile.id}
                        className={`risk-profile-card ${selectedProfile === profile.id ? 'selected' : ''}`}
                        onClick={() => setSelectedProfile(profile.id)}
                        style={{ '--profile-color': profile.color }}
                      >
                        <IconComponent size={24} />
                        <span className="profile-name">{profile.name}</span>
                        <span className="profile-desc">{profile.description}</span>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Error Message */}
              {error && (
                <div className="quantum-error">
                  <AlertCircle size={20} />
                  <span>{error}</span>
                </div>
              )}

              {/* Action Button */}
              <button 
                className="quantum-analyze-btn"
                onClick={fetchRecommendations}
                disabled={loading}
              >
                {loading ? (
                  <>
                    <Loader2 className="spinning" size={20} />
                    Analyzing 131 Stocks...
                  </>
                ) : (
                  <>
                    <Atom size={20} />
                    Run Quantum Analysis
                  </>
                )}
              </button>

              {/* Info */}
              <div className="quantum-info">
                <p>
                  <strong>How it works:</strong> Our quantum algorithm analyzes all 131 stocks 
                  using Qiskit's QAOA-inspired optimization to find the best portfolio allocation 
                  based on your risk tolerance.
                </p>
              </div>
            </>
          ) : (
            <>
              {/* Results */}
              <div className="quantum-results">
                <div className="results-header">
                  <h3>
                    <TrendingUp size={20} />
                    Recommended Portfolio
                  </h3>
                  <span className="profile-badge" style={{ 
                    backgroundColor: riskProfiles.find(p => p.id === selectedProfile)?.color 
                  }}>
                    {selectedProfile.charAt(0).toUpperCase() + selectedProfile.slice(1)}
                  </span>
                </div>

                {/* Recommendations Table */}
                <div className="recommendations-table">
                  <div className="table-header">
                    <span>Stock</span>
                    <span>Sector</span>
                    <span>Allocation</span>
                    <span>Expected Return</span>
                    <span>Risk</span>
                  </div>
                  {recommendations.recommendations.map((rec, index) => (
                    <div key={rec.ticker} className="table-row">
                      <div className="stock-info">
                        <span className="rank">#{index + 1}</span>
                        <div>
                          <span className="ticker">{rec.ticker}</span>
                          <span className="name">{rec.name}</span>
                        </div>
                      </div>
                      <span className="sector">{rec.sector}</span>
                      <div className="allocation">
                        <div className="allocation-bar" style={{ width: `${rec.allocation}%` }} />
                        <span>{rec.allocation}%</span>
                      </div>
                      <span className="return positive">+{rec.expected_return}%</span>
                      <span className={`risk ${rec.risk_level.toLowerCase().replace(' ', '-')}`}>
                        {rec.risk_level}
                      </span>
                    </div>
                  ))}
                </div>

                {/* Portfolio Metrics */}
                <div className="portfolio-metrics">
                  <div className="metric">
                    <span className="metric-label">Expected Annual Return</span>
                    <span className="metric-value positive">
                      {recommendations.portfolio_metrics.expected_annual_return}
                    </span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">Portfolio Risk</span>
                    <span className="metric-value">
                      {recommendations.portfolio_metrics.risk_score}
                    </span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">Sharpe Ratio</span>
                    <span className="metric-value">
                      {recommendations.portfolio_metrics.sharpe_ratio}
                    </span>
                  </div>
                </div>

                {/* Metadata */}
                <div className="quantum-metadata">
                  <span>Method: {recommendations.quantum_method}</span>
                  <span>Stocks Analyzed: {recommendations.metadata.stocks_analyzed}</span>
                </div>

                {/* Action Buttons */}
                <div className="results-actions">
                  <button 
                    className="btn-secondary"
                    onClick={() => setRecommendations(null)}
                  >
                    <RefreshCw size={18} />
                    Try Different Profile
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default QuantumModal;
