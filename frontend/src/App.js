import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Sidebar from './components/layout/Sidebar';
import Dashboard from './pages/Dashboard';
import Stocks from './pages/Stocks';
import StockDetail from './pages/StockDetail';
import Portfolio from './pages/Portfolio';
import Transactions from './pages/Transactions';
import Alerts from './pages/Alerts';
import Login from './pages/Login';
import { getPortfolioSummary } from './api/stockApi';
import './styles/App.css';

function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('portfolio_user');
    return saved ? JSON.parse(saved) : null;
  });

  const handleLogin = (userData) => {
    const u = { id: userData.id, username: userData.username };
    setUser(u);
    localStorage.setItem('portfolio_user', JSON.stringify(u));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('portfolio_user');
  };

  // Refresh user info from server when already logged in
  useEffect(() => {
    if (!user) return;
    const fetchUser = async () => {
      try {
        const response = await getPortfolioSummary(user.id);
        if (response.success) {
          setUser(prev => ({ ...prev, username: response.data.username }));
        }
      } catch (err) {
        console.error('Failed to fetch user:', err);
      }
    };
    fetchUser();
  }, [user?.id]);

  if (!user) {
    return <Login onLogin={handleLogin} />;
  }

  const userId = user.id;

  return (
    <Router>
      <div className="app">
        <Sidebar user={user} onLogout={handleLogout} />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard userId={userId} />} />
            <Route path="/stocks" element={<Stocks userId={userId} />} />
            <Route path="/stocks/:ticker" element={<StockDetail userId={userId} />} />
            <Route path="/portfolio" element={<Portfolio userId={userId} />} />
            <Route path="/transactions" element={<Transactions userId={userId} />} />
            <Route path="/alerts" element={<Alerts userId={userId} />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
