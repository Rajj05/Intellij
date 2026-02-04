import React, { useState, useEffect, useCallback } from 'react';
import { History, ArrowUpRight, ArrowDownRight, Filter } from 'lucide-react';
import Header from '../components/layout/Header';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { getTransactionHistory } from '../api/stockApi';

const Transactions = ({ userId }) => {
  const [transactions, setTransactions] = useState([]);
  const [filteredTransactions, setFilteredTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all'); // 'all', 'buy', 'sell'

  const fetchData = useCallback(async () => {
    try {
      const response = await getTransactionHistory(userId);
      if (response.success) {
        setTransactions(response.data);
        setFilteredTransactions(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch transactions:', err);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    if (filter === 'all') {
      setFilteredTransactions(transactions);
    } else {
      setFilteredTransactions(
        transactions.filter(t => t.transactionType.toLowerCase() === filter)
      );
    }
  }, [filter, transactions]);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return <LoadingSpinner message="Loading transaction history..." />;
  }

  const totalBought = transactions
    .filter(t => t.transactionType === 'BUY')
    .reduce((sum, t) => sum + t.totalAmount, 0);

  const totalSold = transactions
    .filter(t => t.transactionType === 'SELL')
    .reduce((sum, t) => sum + t.totalAmount, 0);

  return (
    <>
      <Header 
        title="Transaction History" 
        subtitle="All your buy and sell transactions"
        userId={userId}
      />
      
      <div className="page-content">
        {/* Summary Stats */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginBottom: '24px' }}>
          <div className="stat-card">
            <p className="stat-card-label">Total Transactions</p>
            <h3 className="stat-card-value">{transactions.length}</h3>
          </div>
          <div className="stat-card">
            <p className="stat-card-label">Total Bought</p>
            <h3 className="stat-card-value" style={{ color: 'var(--profit-green)' }}>
              ${totalBought.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </h3>
          </div>
          <div className="stat-card">
            <p className="stat-card-label">Total Sold</p>
            <h3 className="stat-card-value" style={{ color: 'var(--loss-red)' }}>
              ${totalSold.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </h3>
          </div>
        </div>

        {/* Filters */}
        <div style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
          <div className="tabs">
            <button 
              className={`tab ${filter === 'all' ? 'active' : ''}`}
              onClick={() => setFilter('all')}
            >
              All ({transactions.length})
            </button>
            <button 
              className={`tab ${filter === 'buy' ? 'active' : ''}`}
              onClick={() => setFilter('buy')}
            >
              Buy ({transactions.filter(t => t.transactionType === 'BUY').length})
            </button>
            <button 
              className={`tab ${filter === 'sell' ? 'active' : ''}`}
              onClick={() => setFilter('sell')}
            >
              Sell ({transactions.filter(t => t.transactionType === 'SELL').length})
            </button>
          </div>
        </div>

        {/* Transactions Table */}
        <div className="content-section">
          {filteredTransactions.length > 0 ? (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Stock</th>
                  <th>Type</th>
                  <th>Quantity</th>
                  <th>Price</th>
                  <th>Total</th>
                  <th>Balance After</th>
                </tr>
              </thead>
              <tbody>
                {filteredTransactions.map((tx) => {
                  const isBuy = tx.transactionType === 'BUY';
                  return (
                    <tr key={tx.id}>
                      <td style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                        {formatDate(tx.transactionDate)}
                      </td>
                      <td>
                        <div className="stock-info">
                          <div 
                            className="stock-icon" 
                            style={{ 
                              background: isBuy 
                                ? 'var(--gradient-success)' 
                                : 'var(--gradient-danger)' 
                            }}
                          >
                            {tx.ticker.slice(0, 2)}
                          </div>
                          <div>
                            <div className="stock-name">{tx.ticker}</div>
                            <div className="stock-ticker">{tx.companyName}</div>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span className={`badge ${isBuy ? 'badge-buy' : 'badge-sell'}`}>
                          {isBuy ? (
                            <>
                              <ArrowUpRight size={12} style={{ marginRight: '4px' }} />
                              BUY
                            </>
                          ) : (
                            <>
                              <ArrowDownRight size={12} style={{ marginRight: '4px' }} />
                              SELL
                            </>
                          )}
                        </span>
                      </td>
                      <td>{tx.quantity?.toFixed(2)}</td>
                      <td>${tx.pricePerUnit?.toFixed(2)}</td>
                      <td style={{ 
                        fontWeight: 600, 
                        color: isBuy ? 'var(--loss-red)' : 'var(--profit-green)' 
                      }}>
                        {isBuy ? '-' : '+'}${tx.totalAmount?.toFixed(2)}
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>
                        ${tx.walletBalanceAfter?.toFixed(2)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          ) : (
            <div className="empty-state">
              <History size={48} />
              <h4>No Transactions Yet</h4>
              <p>Your transaction history will appear here once you start trading.</p>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Transactions;
