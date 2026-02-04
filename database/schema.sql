-- Portfolio Manager Database Schema
-- Database: portfolio

USE portfolio;

-- =============================================
-- 1. Users Table
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    wallet_balance DECIMAL(15,2) DEFAULT 50000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- 2. Stocks/Assets Cache Table
-- =============================================
CREATE TABLE IF NOT EXISTS stocks (
    ticker VARCHAR(10) PRIMARY KEY,
    company_name VARCHAR(200),
    current_price DECIMAL(15,2),
    previous_close DECIMAL(15,2),
    day_change DECIMAL(10,2),
    day_change_percent DECIMAL(5,2),
    day_high DECIMAL(15,2),
    day_low DECIMAL(15,2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- 3. Portfolio Holdings Table
-- =============================================
CREATE TABLE IF NOT EXISTS portfolio_holdings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10) NOT NULL,
    quantity DECIMAL(15,6) NOT NULL,
    average_cost DECIMAL(15,2) NOT NULL,
    total_invested DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ticker) REFERENCES stocks(ticker),
    UNIQUE KEY unique_user_stock (user_id, ticker)
);

-- =============================================
-- 4. Transactions Table
-- =============================================
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10) NOT NULL,
    transaction_type ENUM('BUY', 'SELL') NOT NULL,
    quantity DECIMAL(15,6) NOT NULL,
    price_per_unit DECIMAL(15,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    wallet_balance_after DECIMAL(15,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- 5. Alerts Table
-- =============================================
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10),
    alert_type ENUM('PRICE_DROP', 'PRICE_RISE', 'SYSTEM','DAILY_GAIN','DAILY_LOSS','UNDERPERFORMING') NOT NULL,
    threshold DECIMAL(3,1) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- 5. Notifications Table
-- =============================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10),
    notification_type ENUM('PRICE_DROP', 'PRICE_RISE','DAILY_GAIN','DAILY_LOSS','SYSTEM','UNDERPERFORMING') NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- 6. Portfolio Snapshots (for performance graph)
-- =============================================
CREATE TABLE IF NOT EXISTS portfolio_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    snapshot_date DATE NOT NULL,
    total_value DECIMAL(15,2) NOT NULL,
    total_invested DECIMAL(15,2) NOT NULL,
    cash_balance DECIMAL(15,2) NOT NULL,
    daily_gain_loss DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, snapshot_date)
);

-- =============================================
-- Insert Default Test User
-- =============================================
INSERT INTO users (username, email, wallet_balance) 
VALUES ('testuser', 'test@portfolio.com', 50000.00)
ON DUPLICATE KEY UPDATE username = username;

-- =============================================
-- Insert Sample Stocks Data
-- =============================================
INSERT INTO stocks (ticker, company_name, current_price, previous_close, day_change, day_change_percent) VALUES
('AAPL', 'Apple Inc.', 185.50, 183.20, 2.30, 1.26),
('GOOGL', 'Alphabet Inc.', 141.80, 140.50, 1.30, 0.93),
('MSFT', 'Microsoft Corporation', 415.25, 412.00, 3.25, 0.79),
('AMZN', 'Amazon.com Inc.', 178.50, 176.80, 1.70, 0.96),
('TSLA', 'Tesla Inc.', 248.75, 252.30, -3.55, -1.41),
('META', 'Meta Platforms Inc.', 505.20, 498.60, 6.60, 1.32),
('NVDA', 'NVIDIA Corporation', 875.50, 868.00, 7.50, 0.86),
('JPM', 'JPMorgan Chase & Co.', 198.40, 196.80, 1.60, 0.81),
('V', 'Visa Inc.', 285.60, 283.90, 1.70, 0.60),
('JNJ', 'Johnson & Johnson', 158.30, 159.50, -1.20, -0.75),
('WMT', 'Walmart Inc.', 165.80, 164.20, 1.60, 0.97),
('PG', 'Procter & Gamble Co.', 162.45, 161.80, 0.65, 0.40),
('DIS', 'The Walt Disney Company', 112.30, 114.50, -2.20, -1.92),
('NFLX', 'Netflix Inc.', 628.90, 622.40, 6.50, 1.04),
('AMD', 'Advanced Micro Devices', 178.60, 175.30, 3.30, 1.88)
ON DUPLICATE KEY UPDATE 
    company_name = VALUES(company_name),
    current_price = VALUES(current_price),
    previous_close = VALUES(previous_close),
    day_change = VALUES(day_change),
    day_change_percent = VALUES(day_change_percent);

-- =============================================
-- Indexes for Performance
-- =============================================
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
CREATE INDEX idx_alerts_user_read ON alerts(user_id, is_read);
CREATE INDEX idx_snapshots_user_date ON portfolio_snapshots(user_id, snapshot_date);

SELECT 'Database schema created successfully!' AS Status;
