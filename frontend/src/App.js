import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Sidebar from './components/layout/Sidebar';
import Dashboard from './pages/Dashboard';
import Stocks from './pages/Stocks';
import StockDetail from './pages/StockDetail';
import Portfolio from './pages/Portfolio';
import Transactions from './pages/Transactions';
import Alerts from './pages/Alerts';
import { getPortfolioSummary } from './api/stockApi';
import './styles/App.css';

function App() {
  const userId = 1;
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await getPortfolioSummary(userId);
        if (response.success) {
          setUser({
            id: response.data.userId,
            username: response.data.username
          });
        }
      } catch (err) {
        console.error('Failed to fetch user:', err);
      }
    };
    fetchUser();
  }, [userId]);

  return (
    <Router>
      <div className="app">
        <Sidebar user={user} />
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
