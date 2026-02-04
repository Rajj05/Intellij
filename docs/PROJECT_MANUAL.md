# Portfolio Manager - Complete Project Manual

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [Database Design](#4-database-design)
5. [Backend Architecture](#5-backend-architecture)
6. [Frontend Architecture](#6-frontend-architecture)
7. [API Integration](#7-api-integration)
8. [How Components Work Together](#8-how-components-work-together)
9. [Start Script (BAT File)](#9-start-script-bat-file)
10. [GitHub Actions CI/CD](#10-github-actions-cicd)
11. [Running the Application](#11-running-the-application)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Project Overview

**Portfolio Manager** is a full-stack web application that allows users to:
- Track real-time stock prices from the market
- Buy and sell stocks with virtual money (paper trading)
- Monitor portfolio performance with profit/loss calculations
- View historical stock charts (1D, 1W, 1M, 6M, 1Y, 5Y)
- Set price alerts for stocks
- Track all transactions history

### Key Features
| Feature | Description |
|---------|-------------|
| **Dashboard** | Overview of wallet balance, portfolio value, top gainers/losers |
| **Stocks Page** | Browse 131 stocks with live prices, search, and filters |
| **Stock Detail** | Individual stock view with historical charts |
| **Portfolio** | View holdings, profit/loss, buy/sell stocks |
| **Transactions** | Complete history of all buy/sell transactions |
| **Alerts** | Create price alerts for stocks |

---

## 2. Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ (OpenJDK 22) | Programming language |
| **Spring Boot** | 3.2.2 | Web framework |
| **Spring Data JPA** | 3.2.2 | Database ORM |
| **Hibernate** | 6.4.1 | JPA implementation |
| **MySQL Connector** | 8.0.33 | Database driver |
| **Lombok** | 1.18.30 | Boilerplate code reduction |
| **WebFlux** | 6.1.3 | Reactive web client for API calls |
| **Springdoc OpenAPI** | 2.3.0 | Swagger UI documentation |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| **React** | 19.2.4 | UI framework |
| **React Router DOM** | 7.13.0 | Client-side routing |
| **Axios** | 1.13.4 | HTTP client for API calls |
| **Recharts** | 3.7.0 | Stock charts and graphs |
| **Lucide React** | 0.563.0 | Icons library |

### Database
| Technology | Version | Purpose |
|------------|---------|---------|
| **MySQL** | 8.0 | Relational database |

### External API
| API | Purpose |
|-----|---------|
| **Finnhub API** | Real-time stock prices, quotes, and company data |

---

## 3. Project Structure

```
Portfolio_M/
├── backend/                          # Spring Boot Backend
│   ├── pom.xml                       # Maven dependencies
│   ├── src/main/java/com/portfolio/
│   │   ├── PortfolioApplication.java # Main entry point
│   │   ├── config/                   # Configuration classes
│   │   │   ├── CorsConfig.java       # CORS settings
│   │   │   ├── SwaggerConfig.java    # API documentation
│   │   │   └── WebClientConfig.java  # HTTP client config
│   │   ├── controller/               # REST API endpoints
│   │   │   ├── AlertController.java
│   │   │   ├── PortfolioController.java
│   │   │   ├── StockController.java
│   │   │   ├── TransactionController.java
│   │   │   └── UserController.java
│   │   ├── dto/                      # Data Transfer Objects
│   │   │   ├── AlertDTO.java
│   │   │   ├── ApiResponse.java
│   │   │   ├── BuyRequest.java
│   │   │   ├── HoldingDTO.java
│   │   │   ├── PortfolioSummaryDTO.java
│   │   │   ├── SellRequest.java
│   │   │   ├── StockDTO.java
│   │   │   └── TransactionDTO.java
│   │   ├── model/                    # JPA Entities
│   │   │   ├── Alert.java
│   │   │   ├── Notification.java
│   │   │   ├── PortfolioHolding.java
│   │   │   ├── PortfolioSnapshot.java
│   │   │   ├── Stock.java
│   │   │   ├── Transaction.java
│   │   │   └── User.java
│   │   ├── repository/               # Database repositories
│   │   │   ├── AlertRepository.java
│   │   │   ├── PortfolioHoldingRepository.java
│   │   │   ├── StockRepository.java
│   │   │   ├── TransactionRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/                  # Business logic
│   │       ├── AlertService.java
│   │       ├── PortfolioService.java
│   │       ├── StockPriceService.java
│   │       ├── StockService.java
│   │       └── TransactionService.java
│   └── src/main/resources/
│       └── application.properties    # App configuration
│
├── frontend/                         # React Frontend
│   ├── package.json                  # NPM dependencies
│   ├── public/
│   │   └── index.html               # HTML template
│   └── src/
│       ├── App.js                   # Main React component
│       ├── App.css                  # Global styles
│       ├── index.js                 # React entry point
│       ├── api/
│       │   ├── config.js            # API base URL config
│       │   └── stockApi.js          # API call functions
│       ├── components/
│       │   ├── common/
│       │   │   ├── LoadingSpinner.jsx
│       │   │   └── StatCard.jsx
│       │   ├── layout/
│       │   │   ├── Header.jsx
│       │   │   └── Sidebar.jsx
│       │   └── modals/
│       │       ├── AddFundsModal.jsx
│       │       └── TradeModal.jsx
│       ├── pages/
│       │   ├── Alerts.jsx           # Alerts page
│       │   ├── Dashboard.jsx        # Main dashboard
│       │   ├── Portfolio.jsx        # Portfolio page
│       │   ├── StockDetail.jsx      # Individual stock view
│       │   ├── Stocks.jsx           # All stocks list
│       │   └── Transactions.jsx     # Transaction history
│       └── styles/
│           └── App.css              # Component styles
│
├── database/
│   └── schema.sql                   # Database schema
│
├── docs/
│   ├── ER-Diagram.md               # Entity relationship diagram
│   └── PROJECT_MANUAL.md           # This file
│
├── .github/workflows/
│   └── ci.yml                      # GitHub Actions CI/CD
│
├── Start-Portfolio-Manager.bat     # One-click start script
└── README.md                       # Project readme
```

---

## 4. Database Design

### Entity Relationship Diagram

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────┐
│   USERS     │       │ PORTFOLIO_HOLDINGS│       │   STOCKS    │
├─────────────┤       ├──────────────────┤       ├─────────────┤
│ id (PK)     │──┐    │ id (PK)          │    ┌──│ ticker (PK) │
│ username    │  │    │ user_id (FK)     │────┘  │ company_name│
│ email       │  └───>│ ticker (FK)      │       │ current_price│
│ wallet_balance│     │ quantity         │       │ day_change  │
│ created_at  │       │ average_cost     │       │ day_change_%│
│ updated_at  │       │ total_invested   │       │ previous_close│
└─────────────┘       └──────────────────┘       └─────────────┘
      │                                                 │
      │  ┌──────────────────┐                          │
      │  │   TRANSACTIONS   │                          │
      │  ├──────────────────┤                          │
      └─>│ id (PK)          │                          │
         │ user_id (FK)     │                          │
         │ ticker (FK)      │<─────────────────────────┘
         │ type (BUY/SELL)  │
         │ quantity         │
         │ price_per_share  │
         │ total_amount     │
         │ transaction_date │
         └──────────────────┘

      │  ┌──────────────────┐
      │  │     ALERTS       │
      │  ├──────────────────┤
      └─>│ id (PK)          │
         │ user_id (FK)     │
         │ ticker           │
         │ alert_type       │
         │ threshold        │
         │ created_at       │
         └──────────────────┘
```

### Tables Description

#### 1. `users` - User Information
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    wallet_balance DECIMAL(15,2) DEFAULT 100000.00,  -- Starting balance
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 2. `stocks` - Stock Information
```sql
CREATE TABLE stocks (
    ticker VARCHAR(10) PRIMARY KEY,          -- e.g., "AAPL"
    company_name VARCHAR(100) NOT NULL,      -- e.g., "Apple Inc."
    current_price DECIMAL(10,2),             -- Live price from Finnhub
    day_change DECIMAL(10,2),                -- Price change today
    day_change_percent DECIMAL(5,2),         -- % change today
    day_high DECIMAL(10,2),                  -- Today's high
    day_low DECIMAL(10,2),                   -- Today's low
    previous_close DECIMAL(10,2),            -- Yesterday's close
    last_updated TIMESTAMP                   -- When price was updated
);
```

#### 3. `portfolio_holdings` - User's Stock Holdings
```sql
CREATE TABLE portfolio_holdings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,                   -- Number of shares owned
    average_cost DECIMAL(10,2) NOT NULL,     -- Average buy price
    total_invested DECIMAL(15,2) NOT NULL,   -- Total money invested
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ticker) REFERENCES stocks(ticker)
);
```

#### 4. `transactions` - Buy/Sell History
```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10) NOT NULL,
    transaction_type ENUM('BUY', 'SELL') NOT NULL,
    quantity INT NOT NULL,
    price_per_share DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    wallet_balance_after DECIMAL(15,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### 5. `alerts` - Price Alerts
```sql
CREATE TABLE alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10),
    alert_type ENUM('PRICE_DROP', 'PRICE_RISE', 'DAILY_GAIN', 'DAILY_LOSS', 'UNDERPERFORMING') NOT NULL,
    threshold DECIMAL(5,2) NOT NULL,         -- Alert threshold percentage
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 5. Backend Architecture

### 5.1 Main Application Entry Point

**`PortfolioApplication.java`**
```java
@SpringBootApplication
@EnableScheduling  // Enables scheduled tasks for price updates
public class PortfolioApplication {
    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }
}
```

### 5.2 Configuration Classes

#### CORS Configuration (`CorsConfig.java`)
Allows frontend (localhost:3000) to communicate with backend (localhost:8080):
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

#### WebClient Configuration (`WebClientConfig.java`)
Configures HTTP client for Finnhub API calls:
```java
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://finnhub.io/api/v1")
                .build();
    }
}
```

### 5.3 Service Layer - Business Logic

#### Stock Price Service (`StockPriceService.java`)
- Fetches live stock prices from Finnhub API
- Runs every 5 minutes using `@Scheduled(fixedRate = 300000)`
- Updates all 131 stocks in the database

```java
@Scheduled(fixedRate = 300000) // Every 5 minutes
public void updateStockPrices() {
    List<Stock> stocks = stockRepository.findAll();
    for (Stock stock : stocks) {
        // Call Finnhub API
        JsonObject quote = webClient.get()
            .uri("/quote?symbol=" + stock.getTicker() + "&token=" + API_KEY)
            .retrieve()
            .bodyToMono(JsonObject.class)
            .block();
        
        // Update stock with new price
        stock.setCurrentPrice(quote.get("c").getAsBigDecimal());
        stock.setDayChange(quote.get("d").getAsBigDecimal());
        stock.setDayChangePercent(quote.get("dp").getAsBigDecimal());
        stockRepository.save(stock);
    }
}
```

#### Portfolio Service (`PortfolioService.java`)
Handles buy/sell operations:

**Buy Stock:**
1. Check if user has enough wallet balance
2. Deduct amount from wallet
3. Create/update portfolio holding
4. Record transaction

**Sell Stock:**
1. Check if user owns enough shares
2. Add amount to wallet
3. Update/remove portfolio holding
4. Record transaction

### 5.4 Controller Layer - REST APIs

| Controller | Base Path | Purpose |
|------------|-----------|---------|
| `StockController` | `/api/stocks` | Stock data and prices |
| `PortfolioController` | `/api/portfolio` | Portfolio operations |
| `TransactionController` | `/api/transactions` | Transaction history |
| `AlertController` | `/api/alerts` | Price alerts |
| `UserController` | `/api/users` | User management |

### 5.5 API Endpoints

#### Stock Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stocks` | Get all stocks |
| GET | `/api/stocks/{ticker}` | Get single stock |
| GET | `/api/stocks/{ticker}/history?period=1M` | Get historical data |
| GET | `/api/stocks/top-gainers` | Top 5 gainers |
| GET | `/api/stocks/top-losers` | Top 5 losers |

#### Portfolio Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/portfolio/{userId}/summary` | Portfolio summary |
| GET | `/api/portfolio/{userId}/holdings` | All holdings |
| POST | `/api/portfolio/buy` | Buy stocks |
| POST | `/api/portfolio/sell` | Sell stocks |

#### Transaction Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/transactions/user/{userId}` | User's transactions |

#### Alert Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/alerts/{userId}` | Get user's alerts |
| POST | `/api/alerts` | Create alert |
| DELETE | `/api/alerts/{alertId}` | Delete alert |

### 5.6 Data Transfer Objects (DTOs)

DTOs are used to transfer data between frontend and backend:

**`ApiResponse.java`** - Standard API response wrapper:
```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
```

**`BuyRequest.java`** - Buy stock request:
```java
public class BuyRequest {
    private Long userId;
    private String ticker;
    private Integer quantity;
}
```

**`PortfolioSummaryDTO.java`** - Portfolio overview:
```java
public class PortfolioSummaryDTO {
    private BigDecimal walletBalance;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal profitLossPercentage;
    private List<HoldingDTO> holdings;
}
```

---

## 6. Frontend Architecture

### 6.1 Main Components

#### App.js - Main Router
```jsx
function App() {
    const [userId] = useState(1); // Default user
    
    return (
        <Router>
            <div className="app">
                <Sidebar />
                <main className="main-content">
                    <Routes>
                        <Route path="/" element={<Dashboard userId={userId} />} />
                        <Route path="/stocks" element={<Stocks />} />
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
```

### 6.2 API Integration (`stockApi.js`)

All API calls are centralized in `stockApi.js`:

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
});

// Response interceptor - extracts data
api.interceptors.response.use(
    (response) => ({
        success: true,
        data: response.data.data || response.data,
        message: response.data.message
    }),
    (error) => ({
        success: false,
        error: error.response?.data?.message || error.message
    })
);

// API Functions
export const getAllStocks = async () => {
    const response = await api.get('/stocks');
    return response;
};

export const getPortfolioSummary = async (userId) => {
    const response = await api.get(`/portfolio/${userId}/summary`);
    return response;
};

export const buyStock = async (userId, ticker, quantity) => {
    const response = await api.post('/portfolio/buy', { userId, ticker, quantity });
    return response;
};

export const sellStock = async (userId, ticker, quantity) => {
    const response = await api.post('/portfolio/sell', { userId, ticker, quantity });
    return response;
};
```

### 6.3 Page Components

#### Dashboard.jsx
- Displays wallet balance, portfolio value
- Shows top gainers and losers
- Market overview statistics

#### Stocks.jsx
- Lists all 131 stocks
- Search and filter functionality
- Click to view stock detail

#### StockDetail.jsx
- Individual stock information
- Historical price chart (Recharts)
- Period selector (1D, 1W, 1M, 6M, 1Y, 5Y)
- Buy/Sell buttons

#### Portfolio.jsx
- Current holdings with profit/loss
- Total portfolio value
- Trade modal for buy/sell

#### Transactions.jsx
- Complete transaction history
- Filter by type (BUY/SELL)
- Date and amount details

#### Alerts.jsx
- Create price alerts
- View existing alerts
- Delete alerts

### 6.4 Reusable Components

#### StatCard.jsx
Displays statistics in a card format:
```jsx
const StatCard = ({ title, value, icon, trend }) => (
    <div className="stat-card">
        <div className="stat-icon">{icon}</div>
        <div className="stat-info">
            <h3>{title}</h3>
            <p className="stat-value">{value}</p>
            {trend && <span className={`trend ${trend > 0 ? 'positive' : 'negative'}`}>
                {trend > 0 ? '+' : ''}{trend}%
            </span>}
        </div>
    </div>
);
```

#### TradeModal.jsx
Modal for buying/selling stocks:
- Stock selector
- Quantity input
- Price display
- Total calculation
- Submit button

---

## 7. API Integration

### 7.1 Finnhub API

**Base URL:** `https://finnhub.io/api/v1`
**API Key:** Stored in `application.properties`

#### Endpoints Used:

1. **Quote API** - Get current price
```
GET /quote?symbol=AAPL&token={API_KEY}
Response: {
    "c": 150.25,    // Current price
    "d": 2.50,      // Change
    "dp": 1.69,     // Percent change
    "h": 151.00,    // High
    "l": 148.50,    // Low
    "pc": 147.75    // Previous close
}
```

2. **Candles API** - Historical data
```
GET /stock/candle?symbol=AAPL&resolution=D&from={timestamp}&to={timestamp}&token={API_KEY}
Response: {
    "c": [150, 151, 149, ...],  // Close prices
    "h": [152, 153, 150, ...],  // High prices
    "l": [148, 149, 147, ...],  // Low prices
    "o": [149, 150, 148, ...],  // Open prices
    "t": [1234567890, ...],     // Timestamps
    "v": [1000000, ...]         // Volumes
}
```

### 7.2 Rate Limiting

Finnhub free tier: **60 calls/minute**

Our implementation:
- Stocks are updated every 5 minutes (not on every request)
- Historical data is cached
- Batch updates to minimize API calls

### 7.3 Data Flow

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│ Finnhub │────>│ Backend │────>│  MySQL  │────>│ Frontend│
│   API   │     │ Service │     │   DB    │     │  React  │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
     │               │               │               │
     │   HTTP GET    │   JPA Save    │   HTTP GET    │
     │   (5 min)     │               │   (30 sec)    │
     └───────────────┴───────────────┴───────────────┘
```

---

## 8. How Components Work Together

### 8.1 Buy Stock Flow

```
1. User clicks "Buy" on frontend
        ↓
2. TradeModal opens, user enters quantity
        ↓
3. Frontend calls: POST /api/portfolio/buy
   Body: { userId: 1, ticker: "AAPL", quantity: 10 }
        ↓
4. PortfolioController receives request
        ↓
5. PortfolioService.buyStock():
   - Validates user has enough balance
   - Gets current stock price from DB
   - Calculates total: quantity × price
   - Deducts from wallet_balance
   - Creates/updates portfolio_holding
   - Creates transaction record
        ↓
6. Returns PortfolioSummaryDTO to frontend
        ↓
7. Frontend updates UI with new portfolio
```

### 8.2 Stock Price Update Flow

```
1. @Scheduled task runs every 5 minutes
        ↓
2. StockPriceService.updateStockPrices()
        ↓
3. For each stock in database:
   - Call Finnhub API: /quote?symbol={ticker}
   - Parse response JSON
   - Update stock entity
   - Save to database
        ↓
4. Prices now reflect in all API responses
        ↓
5. Frontend polls every 30 seconds
   - Calls /api/portfolio/{userId}/summary
   - Updates displayed prices
```

### 8.3 Historical Chart Flow

```
1. User selects period (e.g., "1M") on StockDetail page
        ↓
2. Frontend calls: GET /api/stocks/AAPL/history?period=1M
        ↓
3. StockService.getStockHistory():
   - Calculates from/to timestamps based on period
   - Calls Finnhub Candles API
   - Transforms data for Recharts
        ↓
4. Returns array of { date, price, high, low, volume }
        ↓
5. Frontend renders Recharts LineChart
```

---

## 9. Start Script (BAT File)

### Location
`C:\Users\Administrator\Desktop\Start-Portfolio-Manager.bat`

### How It Works

```batch
@echo off
title Portfolio Manager Launcher
color 0A

echo ========================================
echo    Portfolio Manager - Starting Up
echo ========================================

:: Start Backend (Spring Boot)
echo [1/3] Starting Backend Server...
cd /d "C:\Users\Administrator\Desktop\Porfolio_M\backend"
start "Portfolio Backend" cmd /k "java -jar target/portfolio-manager-1.0.0.jar"

:: Wait for backend to initialize
echo [2/3] Waiting for backend to start...
timeout /t 15 /nobreak > nul

:: Start Frontend (React)
echo [3/3] Starting Frontend...
cd /d "C:\Users\Administrator\Desktop\Porfolio_M\frontend"
start "Portfolio Frontend" cmd /k "npm start"

:: Wait and open browser
timeout /t 10 /nobreak > nul
start http://localhost:3000

echo ========================================
echo    Application Started Successfully!
echo ========================================
pause
```

### What It Does

1. **Starts Backend Server**
   - Changes to backend directory
   - Runs `java -jar target/portfolio-manager-1.0.0.jar`
   - Opens in separate terminal window

2. **Waits 15 Seconds**
   - Allows Spring Boot to fully initialize
   - Database connections established
   - Scheduled tasks started

3. **Starts Frontend Server**
   - Changes to frontend directory
   - Runs `npm start`
   - React development server starts on port 3000

4. **Opens Browser**
   - Automatically opens http://localhost:3000
   - User sees the application

### Requirements
- Java 17+ installed and in PATH
- Node.js 18+ installed and in PATH
- MySQL running with `portfolio` database
- Backend JAR file built (`mvn package`)
- Frontend dependencies installed (`npm install`)

---

## 10. GitHub Actions CI/CD

### Workflow File
`.github/workflows/ci.yml`

### What It Does

```yaml
name: CI - Build & Test

on:
  push:
    branches: [ main, master, develop ]
  pull_request:
    branches: [ main, master, develop ]

jobs:
  backend:
    # Builds Java backend with Maven
    # - Compiles code
    # - Runs tests (if any)
    # - Creates JAR file
    
  frontend:
    # Builds React frontend
    # - Installs npm dependencies
    # - Runs tests
    # - Creates production build
    
  code-quality:
    # Checks code structure
    # - Counts files
    # - Checks for large files
```

### Triggers
- **Push to main/master/develop**: Automatically runs
- **Pull Requests**: Runs on PR creation/update

### Build Status
- ✅ Green checkmark = Build passed
- ❌ Red X = Build failed (check logs)

---

## 11. Running the Application

### Prerequisites

1. **Java 17+**
   ```bash
   java -version  # Should show 17 or higher
   ```

2. **Node.js 18+**
   ```bash
   node -v  # Should show 18 or higher
   npm -v
   ```

3. **MySQL 8.0**
   ```bash
   mysql -V  # Should show 8.0
   ```

4. **Maven** (for building)
   ```bash
   mvn -v
   ```

### Step-by-Step Setup

#### 1. Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE portfolio;

# Run schema
USE portfolio;
SOURCE C:/Users/Administrator/Desktop/Porfolio_M/database/schema.sql;
```

#### 2. Backend Setup
```bash
cd C:\Users\Administrator\Desktop\Porfolio_M\backend

# Build the project
mvn clean package -DskipTests

# Run the server
java -jar target/portfolio-manager-1.0.0.jar
```

#### 3. Frontend Setup
```bash
cd C:\Users\Administrator\Desktop\Porfolio_M\frontend

# Install dependencies
npm install

# Start development server
npm start
```

#### 4. Access Application
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Quick Start (One-Click)
Double-click `Start-Portfolio-Manager.bat` on Desktop

---

## 12. Troubleshooting

### Common Issues

#### 1. Backend won't start - Port 8080 in use
```powershell
# Find and kill process using port 8080
Get-NetTCPConnection -LocalPort 8080 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

#### 2. Frontend won't start - Port 3000 in use
```powershell
# Find and kill process using port 3000
Get-NetTCPConnection -LocalPort 3000 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

#### 3. Database connection failed
- Check MySQL is running
- Verify credentials in `application.properties`
- Ensure `portfolio` database exists

#### 4. Stock prices not updating
- Check Finnhub API key is valid
- Verify internet connection
- Check rate limiting (60 calls/min)

#### 5. JAR file not found
```bash
cd backend
mvn clean package -DskipTests
```

#### 6. npm install fails
```bash
# Clear cache and reinstall
npm cache clean --force
rm -rf node_modules
npm install --legacy-peer-deps
```

#### 7. Website shows $0.00 everywhere
- Backend is not running
- Start backend first, then refresh frontend

### Logs Location
- **Backend**: Terminal running the JAR
- **Frontend**: Browser Developer Console (F12)

### Useful Commands

```bash
# Check if backend is running
curl http://localhost:8080/api/stocks

# Check database connection
mysql -u root -p portfolio -e "SELECT COUNT(*) FROM stocks;"

# View backend logs
# (visible in terminal where JAR is running)

# Rebuild everything
cd backend && mvn clean package -DskipTests
cd ../frontend && npm run build
```

---

## Appendix A: Environment Variables

### application.properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio
spring.datasource.username=root
spring.datasource.password=n3u3da!

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Finnhub API
finnhub.api.key=d5vpplhr01qihi8nbea0d5vpplhr01qihi8nbeag
```

### Frontend Config (config.js)
```javascript
export const API_BASE_URL = 'http://localhost:8080/api';
```

---

## Appendix B: Stock List

The application includes 131 stocks across categories:
- **Tech Giants**: AAPL, MSFT, GOOGL, AMZN, META, NVDA, etc.
- **Finance**: JPM, BAC, GS, MS, V, MA, etc.
- **Healthcare**: JNJ, PFE, UNH, ABBV, MRK, etc.
- **Consumer**: WMT, COST, HD, NKE, SBUX, etc.
- **Energy**: XOM, CVX, COP, SLB, etc.
- **And more...**

---

## Appendix C: Key Formulas

### Profit/Loss Calculation
```
Current Value = quantity × current_price
Total Invested = quantity × average_cost
Profit/Loss = Current Value - Total Invested
Profit/Loss % = (Profit/Loss / Total Invested) × 100
```

### Average Cost (on new purchase)
```
New Average = (Old Total Invested + New Purchase Amount) / (Old Quantity + New Quantity)
```

### Day Change Calculation
```
Day Change = current_price - previous_close
Day Change % = (Day Change / previous_close) × 100
```

---

**Document Version:** 1.0  
**Last Updated:** February 4, 2026  
**Project Repository:** https://github.com/Rajj05/Portfolio_Management
