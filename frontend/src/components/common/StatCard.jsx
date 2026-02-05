import React from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';

const StatCard = ({ label, value, change, changePercent, icon: Icon, iconColor = 'blue' }) => {
  const isPositive = change >= 0;
  
  return (
    <div className="stat-card">
      <div className="stat-card-header">
        <div>
          <p className="stat-card-label">{label}</p>
          <h3 className="stat-card-value">{value}</h3>
        </div>
        {Icon && (
          <div className={`stat-card-icon ${iconColor}`}>
            <Icon size={22} />
          </div>
        )}
      </div>
      
      {(change !== undefined || changePercent !== undefined) && (
        <div className={`stat-card-change ${isPositive ? 'positive' : 'negative'}`}>
          {isPositive ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
          <span>
            {change !== undefined && `$${Math.abs(change).toFixed(2)}`}
            {changePercent !== undefined && ` (${isPositive ? '+' : ''}${changePercent.toFixed(2)}%)`}
          </span>
        </div>
      )}
    </div>
  );
};

export default StatCard;
