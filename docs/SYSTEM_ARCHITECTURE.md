# Portfolio Management System - Architecture Overview

## System Components

```
┌─────────────────────────────────────────────────────────────┐
│                    PORTFOLIO MANAGER                         │
└─────────────────────────────────────────────────────────────┘

┌──────────────────┬──────────────────┬──────────────────┐
│   FRONTEND       │   BACKEND        │   QUANTUM API    │
│   (React)        │   (Spring Boot)  │   (Flask)        │
│   Port 3000      │   Port 8080      │   Port 5000      │
└──────────────────┴──────────────────┴──────────────────┘
         │                │                    │
         │                │                    │
         └────────────────┴────────────────────┘
                    │
         ┌──────────┴──────────┐
         │                     │
      ┌─────────────┐    ┌─────────────┐
      │  MySQL DB   │    │  Finnhub    │
      │  Port 3306  │    │   API       │
      └─────────────┘    └─────────────┘
```

---

## 1️⃣ Frontend (React) - Port 3000

### Technologies
- React 18.x
- React Router
- Axios (HTTP client)
- Lucide React (Icons)
- CSS3 with responsive design

### Main Features
- 📊 Dashboard with portfolio overview
- 📈 Stock tracking with real-time prices
- 🔔 Notification panel
- ⚠️ Alert management
- 📋 Transaction history
- 💼 Portfolio analytics
- 🚀 Quantum recommendations

### Key Pages
- **Dashboard** (`src/pages/Dashboard.jsx`)
  - Portfolio summary
  - Top gainers/losers
  - Recent transactions

- **Portfolio** (`src/pages/Portfolio.jsx`)
  - Holdings list
  - Current values
  - Buy/Sell modal

- **Stocks** (`src/pages/Stocks.jsx`)
  - Live market data
  - Stock search
  - Stock details

- **Alerts** (`src/pages/Alerts.jsx`)
  - Create price alerts
  - Alert history
  - Alert management

- **Transactions** (`src/pages/Transactions.jsx`)
  - Buy/Sell history
  - Filter by type
  - Transaction details

### Components
- `Header.jsx` - Top navigation with notifications
- `NotificationPanel.jsx` - Floating notification center
- `TradeModal.jsx` - Buy/Sell dialog
- `StatCard.jsx` - Dashboard stats
- `LoadingSpinner.jsx` - Loading states

---

## 2️⃣ Backend (Java Spring Boot) - Port 8080

### Technologies
- Java 17
- Spring Boot 3.2.2
- Spring Data JPA
- MySQL 8.0
- Maven

### REST API Endpoints

#### User Management
```
POST   /api/user              Create user
GET    /api/user/{userId}     Get user info
GET    /api/user/{userId}/wallet              Get wallet
PUT    /api/user/{userId}/wallet              Update wallet
POST   /api/user/{userId}/wallet/reset        Reset wallet
```

#### Stock Management
```
GET    /api/stocks                Get all stocks
GET    /api/stocks/{ticker}       Get stock details
GET    /api/stocks/{ticker}/quote Get live quote
POST   /api/stocks/{ticker}/refresh           Refresh price
POST   /api/stocks/refresh-all    Refresh all
GET    /api/stocks/{ticker}/history?period=1M Historical data
```

#### Portfolio
```
GET    /api/portfolio/{userId}/summary        Portfolio summary
GET    /api/portfolio/{userId}/holdings       User holdings
GET    /api/portfolio/{userId}/transactions   Transaction history
```

#### Transactions
```
POST   /api/transaction/buy     Buy stock
POST   /api/transaction/sell    Sell stock
```

#### Alerts
```
GET    /api/alerts/{userId}    Get user alerts
POST   /api/alerts             Create alert
DELETE /api/alerts/{alertId}   Delete alert
```

#### Notifications
```
GET    /api/notifications/{userId}/unread     Unread notifications
GET    /api/notifications/{userId}            All notifications
PUT    /api/notifications/{notificationId}/read  Mark as read
POST   /api/notifications/test/trigger-alerts    Test endpoint
```

### Database Models

```java
User
├── id
├── username
├── email
├── wallet_balance
└── created_at

Stock
├── ticker (PK)
├── company_name
├── current_price
├── day_change
├── day_change_percent
└── last_updated

Portfolio_Holding
├── id
├── user_id (FK)
├── ticker (FK)
├── quantity
├── average_cost
└── total_invested

Transaction
├── id
├── user_id (FK)
├── ticker
├── type (BUY/SELL)
├── quantity
├── price_per_unit
└── transaction_date

Alert
├── id
├── user_id (FK)
├── ticker
├── alert_type
└── threshold

Notification
├── id
├── user_id (FK)
├── ticker
├── notification_type
├── title
├── message
├── is_read
└── created_at
```

### Services

1. **StockPriceService**
   - Updates all stock prices every 5 minutes
   - Calls Finnhub API
   - Calculates daily changes

2. **AlertNotificationService**
   - Checks alerts every 5 minutes
   - Compares stock data with alert conditions
   - Creates notifications
   - Sends emails

3. **NotificationService**
   - Retrieves notifications
   - Marks as read
   - Manages notification lifecycle

4. **PortfolioService**
   - Calculates portfolio metrics
   - Manages holdings
   - Processes trades

5. **EmailService**
   - Sends emails via Gmail SMTP
   - Formats notification emails
   - Error handling and logging

### Scheduled Tasks

```
Every 5 minutes:
├── StockPriceService.updateAllStockPrices()
│   └── Fetches latest prices from Finnhub
│
└── AlertNotificationService.checkAlertsAndCreateNotifications()
    ├── Checks all alerts against current prices
    ├── Creates notifications if conditions met
    └── Sends emails to users
```

---

## 3️⃣ Quantum API (Python Flask) - Port 5000

### Technologies
- Python 3.9+
- Flask 3.0.0
- Qiskit 1.0.0+
- Qiskit-Aer (simulator)
- NumPy, SciPy

### REST API Endpoints

```
GET    /api/quantum/health              Health check
GET    /api/quantum/profiles            Available risk profiles
GET    /api/quantum/recommend?risk_profile=moderate&num_stocks=5  Get recommendations
```

### Quantum Optimizer Algorithm

**Input:**
- Risk Profile: conservative/moderate/aggressive
- Number of stocks: 3-10

**Process:**
1. Load stock universe (50+ stocks)
2. Calculate Sharpe ratio for each stock
3. Apply quantum interference factor (QAOA-inspired)
4. Score based on:
   - Sharpe ratio (40%)
   - Risk adjustment (30%)
   - Expected return (30%)
5. Apply sector diversification bonus
6. Select top N stocks with rebalancing

**Output:**
```json
{
  "success": true,
  "risk_profile": "moderate",
  "recommendations": [
    {
      "ticker": "AAPL",
      "name": "Apple Inc.",
      "sector": "Technology",
      "allocation": 25,
      "expected_return": 12.0,
      "risk_level": "Medium",
      "quantum_score": 0.8542
    }
  ],
  "portfolio_metrics": {
    "expected_annual_return": "14.2%",
    "risk_score": "18.5%",
    "sharpe_ratio": 0.56,
    "diversification_score": 0.8
  }
}
```

### Stock Universe
- 50+ stocks from multiple sectors
- Includes stocks, ETFs, commodities
- Each with: expected_return, risk, sector

### Risk Profiles

| Profile | Risk Factor | Suitable For |
|---------|------------|--------------|
| Conservative | 0.2 | Risk-averse investors |
| Moderate | 0.5 | Balanced investors |
| Aggressive | 0.8 | Growth-focused |

---

## 4️⃣ Database (MySQL) - Port 3306

### Schema
- 7 main tables
- Multiple indexes for performance
- Foreign key relationships

---

## 🔄 Data Flow Diagram

### User Creates Alert
```
Frontend
  │
  ├─→ POST /api/alerts
  │        ↓
  Backend (AlertController)
  │        ↓
  ├─→ AlertService.createAlert()
  │        ↓
  AlertRepository
  │        ↓
  └─→ Database (alerts table)
```

### System Checks Alerts (Every 5 minutes)
```
StockPriceService (scheduled)
  │
  ├─→ Fetch latest prices from Finnhub
  │        ↓
  ├─→ Update stocks table
  │        ↓
  AlertNotificationService (scheduled, delayed 310s)
  │        ↓
  ├─→ Get all alerts
  │        ↓
  ├─→ For each alert:
  │   ├─→ Get stock data
  │   ├─→ Check if threshold met
  │   └─→ If triggered:
  │       ├─→ Create notification
  │       ├─→ Send email
  │       └─→ Store in database
```

### User Views Notifications
```
Frontend
  │
  ├─→ Open notification panel (click bell)
  │        ↓
  ├─→ GET /api/notifications/{userId}/unread
  │        ↓
  Backend (NotificationController)
  │        ↓
  ├─→ NotificationService.getUnreadNotifications()
  │        ↓
  NotificationRepository
  │        ↓
  ├─→ Query from database
  │        ↓
  └─→ Return JSON to frontend
```

### Get Quantum Recommendations
```
Frontend
  │
  ├─→ GET /api/quantum/recommend?risk_profile=moderate
  │        ↓
  Quantum API (Flask)
  │        ↓
  ├─→ QuantumPortfolioOptimizer
  │   ├─→ Load stock_universe.json
  │   ├─→ Calculate quantum scores
  │   ├─→ Apply QAOA algorithm
  │   ├─→ Diversify sectors
  │   └─→ Generate allocations
  │        ↓
  └─→ Return recommendations JSON
```

---

## 📱 Technology Stack Summary

| Layer | Tech | Port |
|-------|------|------|
| Frontend | React 18, CSS3 | 3000 |
| Backend | Java 17, Spring Boot 3 | 8080 |
| Database | MySQL 8 | 3306 |
| ML/Quantum | Python 3.9, Qiskit | 5000 |
| External APIs | Finnhub, Gmail SMTP | - |

---

## 🔐 Security Features

1. **Database Passwords:** Encrypted in `application.properties`
2. **API Keys:** Finnhub key in config
3. **Email:** App password for Gmail
4. **CORS:** Configured for localhost:3000
5. **Transactions:** Database transactions for data consistency

---

## 📈 Scalability Considerations

### Current Limitations
- Single stock cache (not distributed)
- Synchronous API calls
- No caching layer
- Single database instance

### Future Improvements
1. Add Redis for caching
2. Implement async processing (RabbitMQ)
3. Database replication
4. Load balancing
5. Container deployment (Docker)
6. Kubernetes orchestration

---

## 🧪 Testing Strategy

### Unit Tests
- Service layer logic
- Alert evaluation
- Portfolio calculations

### Integration Tests
- API endpoints
- Database operations
- Email sending

### Manual Testing
- Use Postman for API calls
- Browser developer tools for frontend
- Email inbox for notification delivery

---

## 📊 Monitoring & Logging

### Backend Logging
- All API calls logged
- Alert checks logged
- Email sending logged
- Errors with stack traces

### Frontend Logging
- API request/response logging
- Component lifecycle logs
- Error tracking

### Database Logging
- Slow query logs (if enabled)
- Transaction logs
- Backup logs

---

## 🚀 Deployment Checklist

- [ ] Update all passwords (MySQL, Gmail, API keys)
- [ ] Enable HTTPS
- [ ] Configure firewall
- [ ] Set up backups
- [ ] Enable SSL for email
- [ ] Configure logging aggregation
- [ ] Set up monitoring
- [ ] Create deployment automation

---

This comprehensive system provides a complete portfolio management platform with real-time data, intelligent alerts, quantum-inspired recommendations, and professional-grade architecture.

