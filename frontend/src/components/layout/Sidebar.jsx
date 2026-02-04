import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  TrendingUp,
  Wallet,
  History,
  Bell,
  Settings,
  User
} from 'lucide-react';

const Sidebar = ({ user }) => {
  const navItems = [
    { path: '/', icon: LayoutDashboard, label: 'Dashboard' },
    { path: '/stocks', icon: TrendingUp, label: 'Live Stocks' },
    { path: '/portfolio', icon: Wallet, label: 'My Portfolio' },
    { path: '/transactions', icon: History, label: 'Transactions' },
    { path: '/alerts', icon: Bell, label: 'Alerts' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h1>Portfolio</h1>
        <span>Manager Pro</span>
      </div>

      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          >
            <item.icon />
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-user">
        <div className="user-avatar">
          {user?.username?.charAt(0).toUpperCase() || 'U'}
        </div>
        <div className="user-info">
          <div className="user-name">{user?.username || 'User'}</div>
          <div className="user-role">Investor</div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
