import React, { useState, useEffect } from 'react';
import { X, Clock, AlertCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { getUnreadNotifications, markNotificationAsRead,getStock } from '../../api/stockApi';
import '../styles/NotificationPanel.css';

const NotificationPanel = ({ userId, isOpen, onClose }) => {
    const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && userId) {
      fetchNotifications();
    }
  }, [isOpen, userId]);

  const fetchNotifications = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getUnreadNotifications(userId);
      console.log('Notifications response:', response);
      // Extract the data array from the ApiResponse wrapper
      const notificationsArray = response.data || [];
      setNotifications(Array.isArray(notificationsArray) ? notificationsArray : []);
    } catch (err) {
      setError('Failed to load notifications');
      console.error('Error fetching notifications:', err);
    } finally {
      setLoading(false);
    }
  };


  const handleMarkAsRead = async (notificationId) => {
    try {
      await markNotificationAsRead(notificationId);
      // Remove from list after marking as read
      setNotifications(notifications.filter(n => n.id !== notificationId));
    } catch (err) {
      console.error('Error marking notification as read:', err);
    }
  };

  const getTypeColor = (type) => {
    const typeColors = {
      'DAILY_LOSS': '#d84004',
      'PRICE_DROP': '#ca3204',
      'PRICE_RISE': '#156833',
      'DAILY_GAIN': '#436fb6',
      'SYSTEM': '#4b520f',
      'UNDERPERFORMING': '#703d57'
    };
    return typeColors[type] || '#6b7280';
  };

  const getCompanyName=async(ticker)=>{
    const data=getStock(ticker);
    return data.companyName;
  }
  

  const formatDateTime = (datetime) => {
  if (!datetime) return '';
  
  // Convert the datetime to a Date object
  const date = new Date(datetime);

  // Get the current UTC offset in minutes (for the browser's local timezone)
  const IST_OFFSET = 330;  // 5 hours 30 minutes = 330 minutes

  // Adjust the date to IST by adding the IST offset
  const ISTDate = new Date(date.getTime() + (IST_OFFSET * 60000)); // Convert minutes to milliseconds

  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  const isToday = ISTDate.toDateString() === today.toDateString();
  const isYesterday = ISTDate.toDateString() === yesterday.toDateString();

  // Return formatted time based on whether it's today, yesterday, or earlier
  if (isToday) {
    return ISTDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } else if (isYesterday) {
    return 'Yesterday ' + ISTDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } else {
    return ISTDate.toLocaleDateString([], { month: 'short', day: 'numeric' });
  }
};


  if (!isOpen) return null;

  return (
    <div className="notification-panel-overlay" onClick={onClose}>
      <div className="notification-panel" onClick={(e) => e.stopPropagation()}>
        <div className="notification-header">
          <h3>Notifications</h3>
          <button className="close-btn" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        <div className="notification-content">
          {loading ? (
            <div className="notification-loading">
              <div className="spinner"></div>
              <p>Loading notifications...</p>
            </div>
          ) : error ? (
            <div className="notification-error">
              <AlertCircle size={20} />
              <p>{error}</p>
            </div>
          ) : notifications.length === 0 ? (
            <div className="notification-empty">
              <AlertCircle size={24} />
              <p>No notifications</p>
            </div>
          ) : (
            <div className="notification-list">
              {notifications.map((notification) => (
                <div key={notification.id} className="notification-item">
                  <div className="notification-main">
                    <div className="notification-header-info">
                      <h4>{notification.ticker}</h4>
                      {/* <h4>{getCompanyName(notification.ticker)}</h4> */}
                      <span 
                        className="notification-tag"
                        style={{ 
                          backgroundColor: getTypeColor(notification.notificationType),
                          color: '#fff'
                        }}
                      >
                        {notification.notificationType.replace(/_/g, ' ')}
                      </span>
                    </div>
                    <p className="notification-message">{notification.message}</p>
                    <div className="notification-footer">
                      <span className="notification-time">
                        <Clock size={14} />
                        {formatDateTime(notification.createdAt)}
                      </span>
                      <button 
                        className="action-btn"
                        onClick={() => {
                            handleMarkAsRead(notification.id);
                            navigate(`/stocks/${notification.ticker}`);
                        }}
                      >
                        Take action now
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default NotificationPanel;
