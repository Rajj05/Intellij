import React, { useState, useEffect, useCallback } from 'react';
import { 
  Wallet, 
  TrendingUp, 
  TrendingDown, 
  PieChart, 
  DollarSign, 
  Plus,
  ArrowUpRight,
  ArrowDownRight
} from 'lucide-react';
import Header from '../components/layout/Header';
import StatCard from '../components/common/StatCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import TradeModal from '../components/modals/TradeModal';
import AddFundsModal from '../components/modals/AddFundsModal';
import { getPortfolioSummary, getAllStocks, buyStock, sellStock, updateWalletBalance } from '../api/stockApi';

const Dashboard = ({ userId }) => {
  const [portfolio, setPortfolio] = useState(null);
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);
  
  // Modal states
  const [tradeModal, setTradeModal] = useState({ open: false, stock: null, type: 'buy' });
  const [addFundsModal, setAddFundsModal] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      const [portfolioRes, stocksRes] = await Promise.all([
        getPortfolioSummary(userId),
        getAllStocks()
      ]);
      
      if (portfolioRes.success) {
        setPortfolio(portfolioRes.data);
      }
      if (stocksRes.success) {
        setStocks(stocksRes.data);
      }
      setError(null);
    } catch (err) {
      setError('Failed to fetch data. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchData();
    // Auto-refresh every 30 seconds
    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, [fetchData]);

  const handleRefresh = () => {
    setRefreshing(true);
    fetchData();
  };

  const handleTrade = async ({ ticker, quantity, type }) => {
    const data = {
      userId: userId,
      ticker: ticker,
      quantity: quantity
    };

    if (type === 'buy') {
      await buyStock(data);
    } else {
      await sellStock({ ...data, sellAll: false });
    }
    
    fetchData();
  };

  const handleAddFunds = async (amount) => {
    await updateWalletBalance(userId, amount);
    fetchData();
  };

  const openBuyModal = (stock) => {
    setTradeModal({ open: true, stock, type: 'buy' });
  };

  const openSellModal = (stock) => {
    // Find the holding info for this stock
    const holding = portfolio?.holdings?.find(h => h.ticker === stock.ticker);
    setTradeModal({ 
      open: true, 
      stock: { ...stock, quantity: holding?.quantity || 0 }, 
      type: 'sell' 
    });
  };

  if (loading) {
    return <LoadingSpinner message="Loading your portfolio..." />;
  }

  const topGainers = [...stocks].sort((a, b) => b.dayChangePercent - a.dayChangePercent).slice(0, 5);
  const topLosers = [...stocks].sort((a, b) => a.dayChangePercent - b.dayChangePercent).slice(0, 5);

  return (
    <>
      <Header 
        title="Dashboard" 
        subtitle={`Welcome back, ${portfolio?.username || 'Investor'}!`}
        onRefresh={handleRefresh}
        refreshing={refreshing}
        userId={userId}
      />
      
      <div className="page-content">
        {/* Stats Grid */}
        <div className="dashboard-grid">
          <StatCard
            label="Wallet Balance"
            value={`$${portfolio?.walletBalance?.toLocaleString('en-US', { minimumFractionDigits: 2 }) || '0.00'}`}
            icon={Wallet}
            iconColor="blue"
          />
          <StatCard
            label="Total Invested"
            value={`$${portfolio?.totalInvested?.toLocaleString('en-US', { minimumFractionDigits: 2 }) || '0.00'}`}
            icon={PieChart}
            iconColor="purple"
          />
          <StatCard
            label="Portfolio Value"
            value={`$${portfolio?.totalCurrentValue?.toLocaleString('en-US', { minimumFractionDigits: 2 }) || '0.00'}`}
            change={portfolio?.totalGainLoss}
            changePercent={portfolio?.totalGainLossPercent}
            icon={DollarSign}
            iconColor="green"
          />
          <StatCard
            label="Daily Change"
            value={`$${Math.abs(portfolio?.dailyGainLoss || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}`}
            change={portfolio?.dailyGainLoss}
            changePercent={portfolio?.dailyGainLossPercent}
            icon={portfolio?.dailyGainLoss >= 0 ? TrendingUp : TrendingDown}
            iconColor={portfolio?.dailyGainLoss >= 0 ? 'green' : 'cyan'}
          />
        </div>

        {/* Quick Actions */}
        <div className="quick-actions" style={{ marginBottom: '24px' }}>
          <button className="btn btn-primary" onClick={() => setAddFundsModal(true)}>
            <Plus size={18} />
            Add Funds
          </button>
        </div>

        {/* Two Column Layout */}
        <div className="two-column">
          {/* My Holdings */}
          <div className="content-section">
            <div className="section-header">
              <h3>My Holdings ({portfolio?.totalAssets || 0})</h3>
            </div>
            {portfolio?.holdings?.length > 0 ? (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Stock</th>
                    <th>Quantity</th>
                    <th>Avg Cost</th>
                    <th>Current</th>
                    <th>P/L</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {portfolio.holdings.map((holding) => (
                    <tr key={holding.ticker}>
                      <td>
                        <div className="stock-info">
                          <div className="stock-icon">{holding.ticker.slice(0, 2)}</div>
                          <div>
                            <div className="stock-name">{holding.ticker}</div>
                            <div className="stock-ticker">{holding.companyName}</div>
                          </div>
                        </div>
                      </td>
                      <td>{holding.quantity?.toFixed(2)}</td>
                      <td>${holding.averageCost?.toFixed(2)}</td>
                      <td>${holding.currentPrice?.toFixed(2)}</td>
                      <td>
                        <div className={`price-change ${holding.profitLoss >= 0 ? 'positive' : 'negative'}`}>
                          {holding.profitLoss >= 0 ? <ArrowUpRight size={16} /> : <ArrowDownRight size={16} />}
                          <span>${Math.abs(holding.profitLoss).toFixed(2)} ({holding.profitLossPercent?.toFixed(2)}%)</span>
                        </div>
                      </td>
                      <td>
                        <div className="action-buttons">
                          <button 
                            className="btn btn-success btn-sm"
                            onClick={() => openBuyModal({
                              ticker: holding.ticker,
                              companyName: holding.companyName,
                              currentPrice: holding.currentPrice,
                              dayChangePercent: holding.dayChangePercent
                            })}
                          >
                            Buy
                          </button>
                          <button 
                            className="btn btn-danger btn-sm"
                            onClick={() => openSellModal({
                              ticker: holding.ticker,
                              companyName: holding.companyName,
                              currentPrice: holding.currentPrice,
                              dayChangePercent: holding.dayChangePercent,
                              quantity: holding.quantity
                            })}
                          >
                            Sell
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className="empty-state">
                <PieChart size={48} />
                <h4>No Holdings Yet</h4>
                <p>Start investing by buying your first stock!</p>
              </div>
            )}
          </div>

          {/* Market Overview */}
          <div>
            {/* Top Gainers */}
            <div className="content-section" style={{ marginBottom: '24px' }}>
              <div className="section-header">
                <h3>Top Gainers</h3>
              </div>
              <table className="data-table">
                <tbody>
                  {topGainers.map((stock) => (
                    <tr key={stock.ticker}>
                      <td>
                        <div className="stock-info">
                          <div className="stock-icon" style={{ background: 'var(--gradient-success)' }}>
                            {stock.ticker.slice(0, 2)}
                          </div>
                          <div>
                            <div className="stock-name">{stock.ticker}</div>
                            <div className="stock-ticker">${stock.currentPrice?.toFixed(2)}</div>
                          </div>
                        </div>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div className="price-change positive">
                          <ArrowUpRight size={16} />
                          +{stock.dayChangePercent?.toFixed(2)}%
                        </div>
                      </td>
                      <td style={{ width: '80px' }}>
                        <button 
                          className="btn btn-success btn-sm"
                          onClick={() => openBuyModal(stock)}
                        >
                          Buy
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Top Losers */}
            <div className="content-section">
              <div className="section-header">
                <h3>Top Losers</h3>
              </div>
              <table className="data-table">
                <tbody>
                  {topLosers.map((stock) => (
                    <tr key={stock.ticker}>
                      <td>
                        <div className="stock-info">
                          <div className="stock-icon" style={{ background: 'var(--gradient-danger)' }}>
                            {stock.ticker.slice(0, 2)}
                          </div>
                          <div>
                            <div className="stock-name">{stock.ticker}</div>
                            <div className="stock-ticker">${stock.currentPrice?.toFixed(2)}</div>
                          </div>
                        </div>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div className="price-change negative">
                          <ArrowDownRight size={16} />
                          {stock.dayChangePercent?.toFixed(2)}%
                        </div>
                      </td>
                      <td style={{ width: '80px' }}>
                        <button 
                          className="btn btn-success btn-sm"
                          onClick={() => openBuyModal(stock)}
                        >
                          Buy
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      <TradeModal
        isOpen={tradeModal.open}
        onClose={() => setTradeModal({ open: false, stock: null, type: 'buy' })}
        stock={tradeModal.stock}
        type={tradeModal.type}
        onSubmit={handleTrade}
        walletBalance={portfolio?.walletBalance || 0}
      />

      <AddFundsModal
        isOpen={addFundsModal}
        onClose={() => setAddFundsModal(false)}
        currentBalance={portfolio?.walletBalance || 0}
        onSubmit={handleAddFunds}
      />
    </>
  );
};

export default Dashboard;
