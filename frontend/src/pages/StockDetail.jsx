import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  TrendingUp, 
  TrendingDown, 
  ArrowUpRight, 
  ArrowDownRight,
  Calendar,
  DollarSign,
  BarChart3,
  Activity
} from 'lucide-react';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import Header from '../components/layout/Header';
import LoadingSpinner from '../components/common/LoadingSpinner';
import TradeModal from '../components/modals/TradeModal';
import { getStock, getHistoricalData, getPortfolioSummary, buyStock } from '../api/stockApi';

const PERIODS = [
  { key: '1D', label: '1 Day' },
  { key: '1W', label: '1 Week' },
  { key: '1M', label: '1 Month' },
  { key: '6M', label: '6 Months' },
  { key: '1Y', label: '1 Year' },
  { key: '5Y', label: '5 Years' },
];

const StockDetail = ({ userId }) => {
  const { ticker } = useParams();
  const navigate = useNavigate();
  
  const [stock, setStock] = useState(null);
  const [historicalData, setHistoricalData] = useState(null);
  const [selectedPeriod, setSelectedPeriod] = useState('1M');
  const [loading, setLoading] = useState(true);
  const [chartLoading, setChartLoading] = useState(false);
  const [walletBalance, setWalletBalance] = useState(0);
  const [tradeModal, setTradeModal] = useState({ open: false, stock: null, type: 'buy' });

  const fetchStockData = useCallback(async () => {
    try {
      const [stockRes, portfolioRes] = await Promise.all([
        getStock(ticker),
        getPortfolioSummary(userId)
      ]);
      
      if (stockRes.success) {
        setStock(stockRes.data);
      }
      if (portfolioRes.success) {
        setWalletBalance(portfolioRes.data.walletBalance);
      }
    } catch (err) {
      console.error('Failed to fetch stock:', err);
    }
  }, [ticker, userId]);

  const fetchHistoricalData = useCallback(async (period) => {
    setChartLoading(true);
    try {
      const response = await getHistoricalData(ticker, period);
      if (response.success) {
        setHistoricalData(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch historical data:', err);
    } finally {
      setChartLoading(false);
      setLoading(false);
    }
  }, [ticker]);

  useEffect(() => {
    fetchStockData();
    fetchHistoricalData(selectedPeriod);
  }, [fetchStockData, fetchHistoricalData, selectedPeriod]);

  const handlePeriodChange = (period) => {
    setSelectedPeriod(period);
    fetchHistoricalData(period);
  };

  const handleTrade = async ({ ticker, quantity }) => {
    await buyStock({ userId, ticker, quantity });
    fetchStockData();
  };

  if (loading) {
    return <LoadingSpinner message={`Loading ${ticker} data...`} />;
  }

  const isPositive = historicalData?.periodChangePercent >= 0;
  const chartColor = isPositive ? '#10b981' : '#ef4444';

  // Format chart data
  const chartData = historicalData?.candles?.map(candle => ({
    date: candle.date,
    timestamp: candle.timestamp+19800,
    price: candle.close,
    high: candle.high,
    low: candle.low,
    volume: candle.volume
  })) || [];

  const formatXAxisDate = (timestamp) => {
    if (!timestamp) return '';
    
    try {
      // Convert Unix timestamp (seconds) to milliseconds and create date
      const date = new Date(timestamp * 1000);
      
      // For 1 Day, show only time (HH:MM)
      if (selectedPeriod === '1D') {
        return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
      }
      
      // For 1 Week, show date (Jan 1)
      if (selectedPeriod === '1W') {
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
      }
      
      // For 1 Month, show date (Jan 1)
      if (selectedPeriod === '1M') {
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
      }
      
      // For 6 Months and longer, show date (Jan 1)
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    } catch (err) {
      return timestamp;
    }
  };

  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{
          background: 'var(--card-bg)',
          border: '1px solid var(--border-color)',
          borderRadius: '8px',
          padding: '12px',
          boxShadow: 'var(--shadow-lg)'
        }}>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '8px', fontSize: '0.85rem' }}>
            {label}
          </p>
          <p style={{ color: 'var(--text-primary)', fontWeight: 600, fontSize: '1.1rem' }}>
            ${payload[0].value?.toFixed(2)}
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <>
      <Header 
        title={`${ticker} Stock Details`}
        subtitle={stock?.companyName || ''}
        userId={userId}
      />
      
      <div className="page-content">
        {/* Back Button */}
        <button 
          className="btn btn-outline" 
          onClick={() => navigate(-1)}
          style={{ marginBottom: '20px' }}
        >
          <ArrowLeft size={18} />
          Back
        </button>

        {/* Stock Header */}
        <div className="content-section" style={{ marginBottom: '24px' }}>
          <div style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '20px' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '8px' }}>
                  <div className="stock-icon" style={{ 
                    width: '56px', 
                    height: '56px', 
                    fontSize: '1rem',
                    background: isPositive ? 'var(--gradient-success)' : 'var(--gradient-danger)'
                  }}>
                    {ticker?.slice(0, 2)}
                  </div>
                  <div>
                    <h2 style={{ fontSize: '1.75rem', marginBottom: '4px' }}>{ticker}</h2>
                    <p style={{ color: 'var(--text-secondary)' }}>{stock?.companyName}</p>
                  </div>
                </div>
              </div>
              
              <div style={{ textAlign: 'right' }}>
                <h2 style={{ fontSize: '2rem', marginBottom: '8px' }}>
                  ${stock?.currentPrice?.toFixed(2)}
                </h2>
                <div className={`price-change ${isPositive ? 'positive' : 'negative'}`} style={{ fontSize: '1.1rem' }}>
                  {isPositive ? <ArrowUpRight size={20} /> : <ArrowDownRight size={20} />}
                  <span>
                    ${Math.abs(stock?.dayChange || 0).toFixed(2)} ({stock?.dayChangePercent >= 0 ? '+' : ''}{stock?.dayChangePercent?.toFixed(2)}%) Today
                  </span>
                </div>
              </div>
            </div>
            
            {/* Quick Stats */}
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'repeat(4, 1fr)', 
              gap: '16px',
              marginTop: '24px',
              paddingTop: '24px',
              borderTop: '1px solid var(--border-color)'
            }}>
              <div>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '4px' }}>Previous Close</p>
                <p style={{ fontWeight: 600 }}>${stock?.previousClose?.toFixed(2)}</p>
              </div>
              <div>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '4px' }}>Day High</p>
                <p style={{ fontWeight: 600, color: 'var(--profit-green)' }}>${stock?.dayHigh?.toFixed(2)}</p>
              </div>
              <div>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '4px' }}>Day Low</p>
                <p style={{ fontWeight: 600, color: 'var(--loss-red)' }}>${stock?.dayLow?.toFixed(2)}</p>
              </div>
              <div>
                <button 
                  className="btn btn-success"
                  style={{ width: '100%' }}
                  onClick={() => setTradeModal({ open: true, stock, type: 'buy' })}
                >
                  Buy {ticker}
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Period Selector */}
        <div style={{ marginBottom: '20px' }}>
          <div className="tabs" style={{ display: 'inline-flex' }}>
            {PERIODS.map((period) => (
              <button
                key={period.key}
                className={`tab ${selectedPeriod === period.key ? 'active' : ''}`}
                onClick={() => handlePeriodChange(period.key)}
              >
                {period.label}
              </button>
            ))}
          </div>
        </div>

        {/* Chart Section */}
        <div className="content-section" style={{ marginBottom: '24px' }}>
          <div className="section-header">
            <h3>
              <Activity size={20} style={{ marginRight: '8px', verticalAlign: 'middle' }} />
              Price History - {PERIODS.find(p => p.key === selectedPeriod)?.label}
            </h3>
            <div className={`price-change ${isPositive ? 'positive' : 'negative'}`}>
              {isPositive ? <TrendingUp size={18} /> : <TrendingDown size={18} />}
              <span>
                {historicalData?.periodChangePercent >= 0 ? '+' : ''}
                {historicalData?.periodChangePercent?.toFixed(2)}% this period
              </span>
            </div>
          </div>
          
          <div style={{ padding: '20px', paddingTop: '10px' }}>
            {chartLoading ? (
              <div style={{ height: '400px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <LoadingSpinner message="Loading chart data..." />
              </div>
            ) : (
              <ResponsiveContainer width="100%" height={400}>
                <AreaChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor={chartColor} stopOpacity={0.3}/>
                      <stop offset="95%" stopColor={chartColor} stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                  <XAxis 
                    dataKey="timestamp" 
                    stroke="var(--text-muted)"
                    tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                    tickLine={{ stroke: 'var(--border-color)' }}
                    interval="preserveStartEnd"
                    tickFormatter={formatXAxisDate}
                  />
                  <YAxis 
                    stroke="var(--text-muted)"
                    tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                    tickLine={{ stroke: 'var(--border-color)' }}
                    domain={['auto', 'auto']}
                    tickFormatter={(value) => `$${value.toFixed(0)}`}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Area
                    type="monotone"
                    dataKey="price"
                    stroke={chartColor}
                    strokeWidth={2}
                    fillOpacity={1}
                    fill="url(#colorPrice)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Period Statistics */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px' }}>
          <div className="stat-card">
            <div className="stat-card-header">
              <div>
                <p className="stat-card-label">Period Start</p>
                <h3 className="stat-card-value">${historicalData?.periodStartPrice?.toFixed(2)}</h3>
              </div>
              <div className="stat-card-icon blue">
                <Calendar size={22} />
              </div>
            </div>
          </div>
          
          <div className="stat-card">
            <div className="stat-card-header">
              <div>
                <p className="stat-card-label">Period Change</p>
                <h3 className="stat-card-value" style={{ color: isPositive ? 'var(--profit-green)' : 'var(--loss-red)' }}>
                  {historicalData?.periodChange >= 0 ? '+' : ''}${historicalData?.periodChange?.toFixed(2)}
                </h3>
              </div>
              <div className={`stat-card-icon ${isPositive ? 'green' : 'cyan'}`}>
                {isPositive ? <TrendingUp size={22} /> : <TrendingDown size={22} />}
              </div>
            </div>
          </div>
          
          <div className="stat-card">
            <div className="stat-card-header">
              <div>
                <p className="stat-card-label">Period High</p>
                <h3 className="stat-card-value" style={{ color: 'var(--profit-green)' }}>
                  ${historicalData?.periodHigh?.toFixed(2)}
                </h3>
              </div>
              <div className="stat-card-icon green">
                <BarChart3 size={22} />
              </div>
            </div>
          </div>
          
          <div className="stat-card">
            <div className="stat-card-header">
              <div>
                <p className="stat-card-label">Period Low</p>
                <h3 className="stat-card-value" style={{ color: 'var(--loss-red)' }}>
                  ${historicalData?.periodLow?.toFixed(2)}
                </h3>
              </div>
              <div className="stat-card-icon purple">
                <BarChart3 size={22} />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Trade Modal */}
      <TradeModal
        isOpen={tradeModal.open}
        onClose={() => setTradeModal({ open: false, stock: null, type: 'buy' })}
        stock={tradeModal.stock}
        type={tradeModal.type}
        onSubmit={handleTrade}
        walletBalance={walletBalance}
      />
    </>
  );
};

export default StockDetail;
