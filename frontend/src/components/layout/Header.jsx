import React, { useState } from 'react';
import { RefreshCw, Bell, Atom } from 'lucide-react';
import NotificationPanel from '../modals/NotificationPanel';
// import QuantumModal from '../modals/QuantumModal';
// import '../modals/QuantumModal.css';

const Header = ({ title, subtitle, onRefresh, refreshing, userId }) => {
  const [showNotifications, setShowNotifications] = useState(false);
  const [showQuantum, setShowQuantum] = useState(false);

  const toggleNotifications = () => {
    setShowNotifications(!showNotifications);
  };

  const closeNotifications = () => {
    setShowNotifications(false);
  };

  return (
    <header className="header">
      <div className="header-title">
        <h2>{title}</h2>
        {subtitle && <p>{subtitle}</p>}
      </div>
      
      <div className="header-actions">
        <button 
          className="btn btn-quantum" 
          title="Quantum Portfolio Optimizer"
          onClick={() => setShowQuantum(true)}
        >
          <Atom size={18} />
          <span>Portfolio Optimizer</span>
        </button>

        {onRefresh && (
          <button 
            className="btn btn-outline btn-icon" 
            onClick={onRefresh}
            disabled={refreshing}
            title="Refresh Data"
          >
            <RefreshCw className={refreshing ? 'spinning' : ''} size={18} />
          </button>
        )}
        <button 
          className="btn btn-outline btn-icon" 
          title="Notifications"
          onClick={toggleNotifications}
        >
          <Bell size={18} />
        </button>
      </div>

      <NotificationPanel 
        userId={userId} 
        isOpen={showNotifications} 
        onClose={closeNotifications}
      />
      
      {/* TODO: Enable when Quantum module is ready
      <QuantumModal 
        isOpen={showQuantum} 
        onClose={() => setShowQuantum(false)} 
      />
      */}
    </header>
  );
};

export default Header;
