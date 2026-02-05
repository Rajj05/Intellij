import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, ArrowUpRight, ArrowDownRight, RefreshCw, ChevronRight } from 'lucide-react';
import Header from '../components/layout/Header';
import LoadingSpinner from '../components/common/LoadingSpinner';
import TradeModal from '../components/modals/TradeModal';
import { getAllStocks, refreshAllStocks, getPortfolioSummary } from '../api/stockApi';

const Stocks = ({ userId }) => {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]);
  const [filteredStocks, setFilteredStocks] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [walletBalance, setWalletBalance] = useState(0);
  const [sortConfig, setSortConfig] = useState({ key: 'ticker', direction: 'asc' });
  
  // Modal state
  const [tradeModal, setTradeModal] = useState({ open: false, stock: null, type: 'buy' });

  const fetchData = useCallback(async () => {
    try {
      const [stocksRes, portfolioRes] = await Promise.all([
        getAllStocks(),
        getPortfolioSummary(userId)
      ]);
      
      if (stocksRes.success) {
        setStocks(stocksRes.data);
        setFilteredStocks(stocksRes.data);
      }
      if (portfolioRes.success) {
        setWalletBalance(portfolioRes.data.walletBalance);
      }
    } catch (err) {
      console.error('Failed to fetch stocks:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, [fetchData]);

  useEffect(() => {
    const filtered = stocks.filter(stock => 
      stock.ticker.toLowerCase().includes(searchQuery.toLowerCase()) ||
      stock.companyName.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredStocks(filtered);
  }, [searchQuery, stocks]);

  const handleRefresh = async () => {
    setRefreshing(true);
    try {
      await refreshAllStocks();
      await fetchData();
    } catch (err) {
      console.error('Failed to refresh stocks:', err);
    }
    setRefreshing(false);
  };

  const handleSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });

    const sorted = [...filteredStocks].sort((a, b) => {
      if (a[key] < b[key]) return direction === 'asc' ? -1 : 1;
      if (a[key] > b[key]) return direction === 'asc' ? 1 : -1;
      return 0;
    });
    setFilteredStocks(sorted);
  };

  const getSortIcon = (key) => {
    if (sortConfig.key !== key) return '↕️';
    return sortConfig.direction === 'asc' ? '↑' : '↓';
  };

  if (loading) {
    return <LoadingSpinner message="Loading live stock prices..." />;
  }

  return (
    <>
      <Header 
        title="Live Stocks" 
        subtitle="Real-time market data powered by Finnhub"
        onRefresh={handleRefresh}
        refreshing={refreshing}
        userId={userId}
      />
      
      <div className="page-content">
        {/* Search and Filters */}
        <div style={{ display: 'flex', gap: '16px', marginBottom: '24px', alignItems: 'center' }}>
          <div className="search-input" style={{ flex: 1, maxWidth: '400px' }}>
            <Search size={18} />
            <input
              type="text"
              placeholder="Search stocks by name or ticker..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <span style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
            {filteredStocks.length} stocks available
          </span>
        </div>

        {/* Stocks Table */}
        <div className="content-section">
          <table className="data-table">
            <thead>
              <tr>
                <th onClick={() => handleSort('ticker')} style={{ cursor: 'pointer' }}>
                  Stock {getSortIcon('ticker')}
                </th>
                <th onClick={() => handleSort('currentPrice')} style={{ cursor: 'pointer' }}>
                  Price {getSortIcon('currentPrice')}
                </th>
                <th onClick={() => handleSort('dayChange')} style={{ cursor: 'pointer' }}>
                  Change {getSortIcon('dayChange')}
                </th>
                <th onClick={() => handleSort('dayChangePercent')} style={{ cursor: 'pointer' }}>
                  % Change {getSortIcon('dayChangePercent')}
                </th>
                <th>Day Range</th>
                <th onClick={() => handleSort('previousClose')} style={{ cursor: 'pointer' }}>
                  Prev Close {getSortIcon('previousClose')}
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredStocks.map((stock) => (
                <tr 
                  key={stock.ticker} 
                  onClick={() => navigate(`/stocks/${stock.ticker}`)}
                  style={{ cursor: 'pointer' }}
                  className="clickable-row"
                >
                  <td>
                    <div className="stock-info">
                      <div className="stock-icon">
                        {stock.ticker.slice(0, 2)}
                      </div>
                      <div>
                        <div className="stock-name">{stock.ticker}</div>
                        <div className="stock-ticker">{stock.companyName}</div>
                      </div>
                    </div>
                  </td>
                  <td style={{ fontWeight: 600, fontSize: '1.05rem' }}>
                    ${stock.currentPrice?.toFixed(2)}
                  </td>
                  <td>
                    <div className={`price-change ${stock.dayChange >= 0 ? 'positive' : 'negative'}`}>
                      {stock.dayChange >= 0 ? <ArrowUpRight size={16} /> : <ArrowDownRight size={16} />}
                      ${Math.abs(stock.dayChange)?.toFixed(2)}
                    </div>
                  </td>
                  <td>
                    <span 
                      className={`badge ${stock.dayChangePercent >= 0 ? 'badge-buy' : 'badge-sell'}`}
                      style={{ fontSize: '0.85rem', padding: '6px 12px' }}
                    >
                      {stock.dayChangePercent >= 0 ? '+' : ''}{stock.dayChangePercent?.toFixed(2)}%
                    </span>
                  </td>
                  <td style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                    ${stock.dayLow?.toFixed(2)} - ${stock.dayHigh?.toFixed(2)}
                  </td>
                  <td>${stock.previousClose?.toFixed(2)}</td>
                  <td>
                    <div className="action-buttons" style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                      <button 
                        className="btn btn-success btn-sm"
                        onClick={(e) => {
                          e.stopPropagation();
                          setTradeModal({ open: true, stock, type: 'buy' });
                        }}
                      >
                        Buy
                      </button>
                      <ChevronRight size={18} style={{ color: 'var(--text-secondary)' }} />
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Market Stats */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(3, 1fr)', 
          gap: '16px',
          marginTop: '24px'
        }}>
          <div className="stat-card">
            <p className="stat-card-label">📈 Gainers Today</p>
            <h3 className="stat-card-value" style={{ color: 'var(--profit-green)' }}>
              {stocks.filter(s => s.dayChangePercent > 0).length}
            </h3>
          </div>
          <div className="stat-card">
            <p className="stat-card-label">📉 Losers Today</p>
            <h3 className="stat-card-value" style={{ color: 'var(--loss-red)' }}>
              {stocks.filter(s => s.dayChangePercent < 0).length}
            </h3>
          </div>
          <div className="stat-card">
            <p className="stat-card-label">➡️ Unchanged</p>
            <h3 className="stat-card-value" style={{ color: 'var(--text-secondary)' }}>
              {stocks.filter(s => s.dayChangePercent === 0).length}
            </h3>
          </div>
        </div>
      </div>

      {/* Trade Modal */}
      <TradeModal
        isOpen={tradeModal.open}
        onClose={() => setTradeModal({ open: false, stock: null, type: 'buy' })}
        stock={tradeModal.stock}
        type={tradeModal.type}
        onSubmit={async ({ ticker, quantity }) => {
          const { buyStock } = await import('../api/stockApi');
          await buyStock({ userId, ticker, quantity });
          fetchData();
        }}
        walletBalance={walletBalance}
      />
    </>
  );
};

export default Stocks;
