import React, { useState, useEffect, useCallback } from 'react';
import { PieChart, TrendingUp, TrendingDown, DollarSign, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import Header from '../components/layout/Header';
import StatCard from '../components/common/StatCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import TradeModal from '../components/modals/TradeModal';
import { getPortfolioSummary, buyStock, sellStock } from '../api/stockApi';

const Portfolio = ({ userId }) => {
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  
  // Modal state
  const [tradeModal, setTradeModal] = useState({ open: false, stock: null, type: 'buy' });

  const fetchData = useCallback(async () => {
    try {
      const response = await getPortfolioSummary(userId);
      if (response.success) {
        setPortfolio(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch portfolio:', err);
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
      // await sellStock({ ...data, sellAll: false });
      await sellStock({ ...data});
    }
    
    fetchData();
  };

  if (loading) {
    return <LoadingSpinner message="Loading your portfolio..." />;
  }

  const holdings = portfolio?.holdings || [];
  const totalValue = portfolio?.totalCurrentValue || 0;

  return (
    <>
      <Header 
        title="My Portfolio" 
        subtitle="Your investment overview"
        onRefresh={handleRefresh}
        refreshing={refreshing}
        userId={userId}
      />
      
      <div className="page-content">
        {/* Stats */}
        <div className="dashboard-grid">
          <StatCard
            label="Total Value"
            value={`$${totalValue.toLocaleString('en-US', { minimumFractionDigits: 2 })}`}
            change={portfolio?.totalGainLoss}
            changePercent={portfolio?.totalGainLossPercent}
            icon={DollarSign}
            iconColor="green"
          />
          <StatCard
            label="Total Invested"
            value={`$${portfolio?.totalInvested?.toLocaleString('en-US', { minimumFractionDigits: 2 }) || '0.00'}`}
            icon={PieChart}
            iconColor="purple"
          />
          <StatCard
            label="Daily P/L"
            value={`$${Math.abs(portfolio?.dailyGainLoss || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}`}
            change={portfolio?.dailyGainLoss}
            changePercent={portfolio?.dailyGainLossPercent}
            icon={portfolio?.dailyGainLoss >= 0 ? TrendingUp : TrendingDown}
            iconColor={portfolio?.dailyGainLoss >= 0 ? 'green' : 'cyan'}
          />
          <StatCard
            label="Total Assets"
            value={portfolio?.totalAssets || 0}
            icon={PieChart}
            iconColor="blue"
          />
        </div>

        {/* Holdings */}
        <div className="content-section">
          <div className="section-header">
            <h3>Holdings ({holdings.length})</h3>
          </div>
          
          {holdings.length > 0 ? (
            <div className="holdings-grid">
              {holdings.map((holding) => {
                const isPositive = holding.profitLoss >= 0;
                const allocation = totalValue > 0 
                  ? ((holding.currentValue / totalValue) * 100).toFixed(1) 
                  : 0;
                
                return (
                  <div key={holding.ticker} className="holding-card">
                    <div className="holding-header">
                      <div className="stock-info">
                        <div 
                          className="stock-icon" 
                          style={{ 
                            background: isPositive 
                              ? 'var(--gradient-success)' 
                              : 'var(--gradient-danger)' 
                          }}
                        >
                          {holding.ticker.slice(0, 2)}
                        </div>
                        <div>
                          <div className="stock-name">{holding.ticker}</div>
                          <div className="stock-ticker">{holding.companyName}</div>
                        </div>
                      </div>
                      <div className={`price-change ${isPositive ? 'positive' : 'negative'}`}>
                        {isPositive ? <ArrowUpRight size={16} /> : <ArrowDownRight size={16} />}
                        {holding.profitLossPercent?.toFixed(2)}%
                      </div>
                    </div>

                    <div className="holding-stats">
                      <div className="holding-stat">
                        <div className="holding-stat-label">Quantity</div>
                        <div className="holding-stat-value">{holding.quantity?.toFixed(2)}</div>
                      </div>
                      <div className="holding-stat">
                        <div className="holding-stat-label">Avg Cost</div>
                        <div className="holding-stat-value">${holding.averageCost?.toFixed(2)}</div>
                      </div>
                      <div className="holding-stat">
                        <div className="holding-stat-label">Current Price</div>
                        <div className="holding-stat-value">${holding.currentPrice?.toFixed(2)}</div>
                      </div>
                      <div className="holding-stat">
                        <div className="holding-stat-label">Market Value</div>
                        <div className="holding-stat-value">${holding.currentValue?.toFixed(2)}</div>
                      </div>
                      <div className="holding-stat">
                        <div className="holding-stat-label">P/L</div>
                        <div 
                          className="holding-stat-value" 
                          style={{ color: isPositive ? 'var(--profit-green)' : 'var(--loss-red)' }}
                        >
                          {isPositive ? '+' : ''}${holding.profitLoss?.toFixed(2)}
                        </div>
                      </div>
                      <div className="holding-stat">
                        <div className="holding-stat-label">Allocation</div>
                        <div className="holding-stat-value">{allocation}%</div>
                      </div>
                    </div>

                    <div className="action-buttons" style={{ marginTop: '16px' }}>
                      <button 
                        className="btn btn-success btn-sm" 
                        style={{ flex: 1 }}
                        onClick={() => setTradeModal({
                          open: true,
                          stock: {
                            ticker: holding.ticker,
                            companyName: holding.companyName,
                            currentPrice: holding.currentPrice,
                            dayChangePercent: holding.dayChangePercent
                          },
                          type: 'buy'
                        })}
                      >
                        Buy More
                      </button>
                      <button 
                        className="btn btn-danger btn-sm" 
                        style={{ flex: 1 }}
                        onClick={() => setTradeModal({
                          open: true,
                          stock: {
                            ticker: holding.ticker,
                            companyName: holding.companyName,
                            currentPrice: holding.currentPrice,
                            dayChangePercent: holding.dayChangePercent,
                            quantity: holding.quantity
                          },
                          type: 'sell'
                        })}
                      >
                        Sell
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="empty-state">
              <PieChart size={48} />
              <h4>No Holdings Yet</h4>
              <p>Start building your portfolio by buying stocks from the market!</p>
              <a href="/stocks" className="btn btn-primary" style={{ marginTop: '16px' }}>
                Browse Stocks
              </a>
            </div>
          )}
        </div>
      </div>

      {/* Trade Modal */}
      <TradeModal
        isOpen={tradeModal.open}
        onClose={() => setTradeModal({ open: false, stock: null, type: 'buy' })}
        stock={tradeModal.stock}
        type={tradeModal.type}
        onSubmit={handleTrade}
        walletBalance={portfolio?.walletBalance || 0}
      />
    </>
  );
};

export default Portfolio;
