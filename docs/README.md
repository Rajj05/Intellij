# Portfolio Manager - Project Ideas & Features

## Inspiration from Industry-Standard Portfolio Managers

Based on popular portfolio management applications (Personal Capital, Mint, Yahoo Finance, etc.)

---

## 🎯 Core Features (Priority for Your Project)

### 1. Portfolio Management
- **Create multiple portfolios** (e.g., "Retirement", "Trading", "Long-term")
- **Add/Remove assets** (stocks, ETFs, bonds)
- **Track quantity and purchase price**
- **View total portfolio value**
- **Cash balance tracking**

### 2. Transaction History
- **Buy transactions**: Record stock purchases
- **Sell transactions**: Record stock sales
- **Transaction log**: View all historical trades
- **Cost basis calculation**: Average cost method
- **Profit/Loss calculation**: Per transaction

### 3. Portfolio Analytics
- **Total portfolio value**: Sum of all holdings
- **Total gain/loss**: Current value vs invested amount
- **Percentage gain/loss**: ROI calculation
- **Asset allocation**: Pie chart of holdings
- **Sector allocation**: Group by industry sectors
- **Performance over time**: Line graph

### 4. Live Market Data Integration
- **Fetch current prices**: Yahoo Finance API / Alpha Vantage
- **Auto-update prices**: Refresh on page load
- **Display price changes**: Daily +/- percentages
- **Market status**: Is market open/closed?

---

## 🚀 Phase-by-Phase Feature Rollout

### **Phase 1: Basic CRUD (Week 1-2)**
✅ Create portfolio item (ticker, quantity, price)  
✅ Read all portfolio items  
✅ Update portfolio item  
✅ Delete portfolio item  
✅ Simple HTML table display  

**API Endpoints:**
- `POST /api/portfolio` - Add item
- `GET /api/portfolio` - List all items
- `GET /api/portfolio/{id}` - Get single item
- `PUT /api/portfolio/{id}` - Update item
- `DELETE /api/portfolio/{id}` - Remove item

---

### **Phase 2: Multiple Portfolios (Week 3)**
✅ Create named portfolios  
✅ Add items to specific portfolios  
✅ Switch between portfolios  
✅ Portfolio summary dashboard  

**API Endpoints:**
- `POST /api/portfolios` - Create portfolio
- `GET /api/portfolios` - List all portfolios
- `POST /api/portfolios/{id}/items` - Add item to portfolio

---

### **Phase 3: Live Prices (Week 4)**
✅ Integrate Yahoo Finance API  
✅ Fetch current stock prices  
✅ Display current value vs purchase price  
✅ Calculate gain/loss  
✅ Color code gains (green) and losses (red)  

**API Endpoints:**
- `GET /api/assets/{ticker}/price` - Get live price
- `GET /api/portfolio/{id}/value` - Calculate total value

**External API:**
- Yahoo Finance: `https://query1.finance.yahoo.com/v8/finance/chart/{TICKER}`
- Alpha Vantage: `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={TICKER}`

---

### **Phase 4: Transaction History (Week 5)**
✅ Record buy/sell transactions  
✅ View transaction log  
✅ Calculate average cost basis  
✅ Track realized vs unrealized gains  

**API Endpoints:**
- `POST /api/transactions` - Record transaction
- `GET /api/portfolio/{id}/transactions` - View history

---

### **Phase 5: Charts & Visualizations (Week 6)**
✅ Portfolio performance line chart  
✅ Asset allocation pie chart  
✅ Sector allocation donut chart  
✅ Historical price charts  

**JavaScript Libraries:**
- Chart.js (simple, lightweight)
- D3.js (powerful, complex)
- ApexCharts (modern, beautiful)

---

### **Phase 6: Advanced Features (Optional)**
✅ Price alerts (notify when stock hits target)  
✅ Dividend tracking  
✅ Portfolio rebalancing suggestions  
✅ Tax lot tracking (FIFO, LIFO)  
✅ Export to CSV/Excel  
✅ Portfolio comparison  

---

## 💡 Feature Ideas Breakdown

### A. Dashboard View
```
┌─────────────────────────────────────────┐
│  My Portfolio - $125,450.00 (+$5,230)  │
├─────────────────────────────────────────┤
│  Total Invested: $120,220.00           │
│  Current Value:  $125,450.00           │
│  Total Gain:     $5,230.00 (+4.35%)    │
├─────────────────────────────────────────┤
│  Holdings:                              │
│  - AAPL: 50 shares @ $175.50           │
│  - GOOGL: 20 shares @ $140.25          │
│  - MSFT: 30 shares @ $380.75           │
└─────────────────────────────────────────┘
```

### B. Transaction Log View
```
Date       | Action | Ticker | Qty | Price   | Total
-----------|--------|--------|-----|---------|----------
2026-01-15 | BUY    | AAPL   | 10  | $175.50 | $1,755.00
2026-01-10 | BUY    | GOOGL  | 20  | $140.25 | $2,805.00
2026-01-05 | SELL   | TSLA   | 5   | $220.00 | $1,100.00
```

### C. Asset Allocation Chart
```
Pie Chart:
- Technology: 60%
- Finance: 25%
- Healthcare: 10%
- Cash: 5%
```

---

## 🔧 Technical Implementation Ideas

### 1. Backend Architecture (Java Spring Boot)

```
Controller Layer
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Database Access)
    ↓
Database (MySQL/PostgreSQL)
```

**Key Classes:**
- `PortfolioController.java` - REST endpoints
- `PortfolioService.java` - Business logic
- `PortfolioRepository.java` - Data access
- `PortfolioItem.java` - Entity model
- `Transaction.java` - Transaction entity
- `Asset.java` - Stock/asset entity

### 2. Frontend Architecture (HTML/CSS/JS)

```
index.html
    ↓
portfolio.js (fetch API calls)
    ↓
portfolio.css (styling)
```

**Key Files:**
- `index.html` - Dashboard
- `portfolio.html` - Portfolio details
- `transactions.html` - Transaction log
- `app.js` - Main JavaScript
- `api-service.js` - API wrapper
- `chart-config.js` - Chart.js setup

### 3. Database Tables (Minimal Start)

```sql
portfolios
    ├── id
    ├── name
    └── cash_balance

portfolio_items
    ├── id
    ├── portfolio_id (FK)
    ├── ticker
    ├── quantity
    └── purchase_price

transactions
    ├── id
    ├── portfolio_item_id (FK)
    ├── type (BUY/SELL)
    ├── quantity
    ├── price
    └── date
```

---

## 📊 Data Sources & APIs

### Free Stock Market APIs

1. **Yahoo Finance API** (Recommended)
   - Free, no API key needed
   - Real-time quotes
   - Historical data
   - Example: `https://query1.finance.yahoo.com/v8/finance/chart/AAPL`

2. **Alpha Vantage** (Good alternative)
   - Free tier: 25 requests/day
   - Requires API key (free)
   - URL: `https://www.alphavantage.co/`

3. **IEX Cloud** (Premium features)
   - Free tier available
   - More reliable
   - URL: `https://iexcloud.io/`

4. **Finnhub** (Good for real-time)
   - Free tier: 60 API calls/minute
   - WebSocket support
   - URL: `https://finnhub.io/`

### Python Integration (ML Features)

**Price Prediction Model:**
```python
# Simple Linear Regression Example
import pandas as pd
from sklearn.linear_model import LinearRegression

def predict_price(ticker, days_ahead):
    # Fetch historical data
    # Train model
    # Predict future price
    return predicted_price
```

**Portfolio Optimization:**
```python
# Using Modern Portfolio Theory
import numpy as np
import scipy.optimize as optimization

def optimize_portfolio(tickers, weights):
    # Calculate expected returns
    # Calculate volatility
    # Optimize Sharpe ratio
    return optimal_weights
```

---

## 🎨 UI/UX Ideas

### Color Scheme
- **Positive gains**: Green (#22c55e)
- **Negative losses**: Red (#ef4444)
- **Neutral**: Gray (#6b7280)
- **Primary actions**: Blue (#3b82f6)

### Key UI Components
1. **Dashboard Cards**: Show key metrics
2. **Data Tables**: Sortable, filterable
3. **Charts**: Interactive, responsive
4. **Forms**: Add/Edit items
5. **Modals**: Confirm delete actions

### Responsive Design
- Desktop: 3-column layout
- Tablet: 2-column layout
- Mobile: Single column, stacked

---

## 🔐 Security Considerations (Future)

1. **Input Validation**: Sanitize all user inputs
2. **SQL Injection Prevention**: Use parameterized queries
3. **CORS Configuration**: Limit allowed origins
4. **Rate Limiting**: Prevent API abuse
5. **Authentication**: JWT tokens (for multi-user)

---

## 🧪 Testing Strategy

### Unit Tests (JUnit)
```java
@Test
public void testCalculateTotalValue() {
    PortfolioItem item = new PortfolioItem("AAPL", 10, 150.0);
    assertEquals(1500.0, item.getTotalValue());
}
```

### Integration Tests
- Test REST endpoints with MockMvc
- Test database operations with H2 in-memory DB

### Frontend Tests
- Manual testing with Postman
- Browser console testing

---

## 📈 Performance Metrics to Track

1. **Portfolio Value**: Current total worth
2. **Total Gain/Loss**: Profit or loss amount
3. **Percentage Return**: ROI percentage
4. **Daily Change**: Today's gain/loss
5. **Asset Allocation**: Distribution by type/sector
6. **Best/Worst Performers**: Top gainers/losers

---

## 🚦 Project Milestones

### Milestone 1: Hello World (Day 1-2)
- Spring Boot app running
- Single GET endpoint working
- Returns JSON response

### Milestone 2: Basic CRUD (Week 1)
- All CRUD endpoints working
- Data persists in database
- Tested with Postman

### Milestone 3: Simple Frontend (Week 2)
- HTML page displays data
- Add/Delete functionality
- Basic styling

### Milestone 4: Live Prices (Week 3)
- Fetch prices from API
- Display current values
- Calculate gains/losses

### Milestone 5: Polish & Deploy (Week 4)
- Charts and visualizations
- Improved UI/UX
- Ready for demo

---

## 💻 Recommended Tech Stack Summary

### Backend
- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0 or PostgreSQL 15
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **API Docs**: Swagger/OpenAPI

### Frontend
- **Core**: HTML5, CSS3, JavaScript (ES6+)
- **UI Framework**: Bootstrap 5 or Tailwind CSS
- **Charts**: Chart.js or ApexCharts
- **HTTP Client**: Fetch API or Axios

### Optional Python ML Service
- **Framework**: Flask or FastAPI
- **ML Libraries**: scikit-learn, pandas, numpy
- **Deployment**: Run as separate microservice

### Development Tools
- **IDE**: VS Code or IntelliJ IDEA
- **API Testing**: Postman
- **Version Control**: Git + GitHub
- **Database Client**: DBeaver or MySQL Workbench
- **Task Management**: Trello or GitHub Projects

---

## 🎓 Learning Outcomes

By completing this project, you will learn:

1. ✅ **Backend Development**: REST API design, Spring Boot
2. ✅ **Database Design**: ER diagrams, SQL, JPA
3. ✅ **Frontend Development**: HTML/CSS/JS, API integration
4. ✅ **Version Control**: Git branching, pull requests
5. ✅ **Team Collaboration**: Code reviews, task management
6. ✅ **API Integration**: External data sources
7. ✅ **Data Visualization**: Charts and graphs
8. ✅ **Software Architecture**: Layered design patterns

---

## 📚 Helpful Resources

### Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- JPA/Hibernate: https://hibernate.org/orm/documentation/
- Chart.js: https://www.chartjs.org/docs/

### Tutorials
- Spring Boot REST API: https://spring.io/guides/gs/rest-service/
- React (optional frontend): https://react.dev/learn

### Inspiration
- Yahoo Finance: https://finance.yahoo.com/
- Personal Capital: https://www.personalcapital.com/
- Robinhood: https://robinhood.com/

---

## ✅ Success Criteria

Your project is successful if:

1. ✅ You can add stocks to a portfolio via REST API
2. ✅ You can view all portfolio items in a browser
3. ✅ You can delete portfolio items
4. ✅ Data persists in a database
5. ✅ Live stock prices are displayed (bonus)
6. ✅ Team members collaborated using Git
7. ✅ Code is well-organized and documented

---

## 🎯 Final Tips

1. **Start Small**: Get ONE feature working perfectly before adding more
2. **Test Often**: Test each endpoint with Postman immediately
3. **Commit Frequently**: Small, meaningful Git commits
4. **Communicate Daily**: Team standups to sync progress
5. **Ask for Help**: Use instructors, Stack Overflow, documentation
6. **Have Fun**: This is a practical, useful application!

Good luck with your Portfolio Manager project! 🚀📈
