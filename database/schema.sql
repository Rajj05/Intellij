USE portfolio;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    wallet_balance DECIMAL(15,2) DEFAULT 50000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(10),
    alert_type ENUM('PRICE_DROP', 'PRICE_RISE', 'SYSTEM','DAILY_GAIN','DAILY_LOSS','UNDERPERFORMING') NOT NULL,
    threshold DECIMAL(3,1) NOT NULL,
    title VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

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

INSERT INTO users (username, email, wallet_balance) 
VALUES ('testuser', 'test@portfolio.com', 50000.00)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO stocks (ticker, company_name, current_price, previous_close, day_change, day_change_percent) VALUES
('AAPL', 'Apple Inc.', 185.50, 183.20, 2.30, 1.26),
('ADI', 'Analog Devices Inc.', 198.75, 196.50, 2.25, 1.15),
('AMAT', 'Applied Materials Inc.', 168.40, 165.80, 2.60, 1.57),
('AMD', 'Advanced Micro Devices', 178.60, 175.30, 3.30, 1.88),
('AVGO', 'Broadcom Inc.', 1285.50, 1272.30, 13.20, 1.04),
('CDNS', 'Cadence Design Systems', 285.60, 282.40, 3.20, 1.13),
('CFLT', 'Confluent Inc.', 28.45, 27.80, 0.65, 2.34),
('CRWD', 'CrowdStrike Holdings Inc.', 312.80, 308.50, 4.30, 1.39),
('CSCO', 'Cisco Systems Inc.', 52.30, 51.85, 0.45, 0.87),
('DDOG', 'Datadog Inc.', 125.40, 122.80, 2.60, 2.12),
('DOCU', 'DocuSign Inc.', 58.90, 57.40, 1.50, 2.61),
('ESTC', 'Elastic NV', 112.50, 109.80, 2.70, 2.46),
('GOOGL', 'Alphabet Inc.', 141.80, 140.50, 1.30, 0.93),
('HUBS', 'HubSpot Inc.', 625.80, 618.50, 7.30, 1.18),
('IBM', 'International Business Machines', 188.90, 187.20, 1.70, 0.91),
('KLAC', 'KLA Corp.', 685.40, 678.20, 7.20, 1.06),
('LRCX', 'Lam Research Corp.', 925.60, 915.30, 10.30, 1.13),
('MCHP', 'Microchip Technology Inc.', 82.50, 81.20, 1.30, 1.60),
('MDB', 'MongoDB Inc.', 285.40, 280.60, 4.80, 1.71),
('META', 'Meta Platforms Inc.', 505.20, 498.60, 6.60, 1.32),
('MRVL', 'Marvell Technology Inc.', 72.80, 71.20, 1.60, 2.25),
('MSFT', 'Microsoft Corporation', 415.25, 412.00, 3.25, 0.79),
('MU', 'Micron Technology Inc.', 98.50, 96.80, 1.70, 1.76),
('NET', 'Cloudflare Inc.', 92.40, 90.50, 1.90, 2.10),
('NOW', 'ServiceNow Inc.', 785.60, 778.40, 7.20, 0.93),
('NVDA', 'NVIDIA Corporation', 875.50, 868.00, 7.50, 0.86),
('NXPI', 'NXP Semiconductors NV', 248.60, 245.80, 2.80, 1.14),
('OKTA', 'Okta Inc.', 98.50, 96.20, 2.30, 2.39),
('ON', 'ON Semiconductor Corp.', 72.40, 71.20, 1.20, 1.69),
('PANW', 'Palo Alto Networks Inc.', 325.80, 321.50, 4.30, 1.34),
('PATH', 'UiPath Inc.', 18.90, 18.40, 0.50, 2.72),
('PLTR', 'Palantir Technologies', 22.50, 21.80, 0.70, 3.21),
('QCOM', 'Qualcomm Inc.', 168.40, 166.20, 2.20, 1.32),
('SHOP', 'Shopify Inc.', 78.50, 76.80, 1.70, 2.21),
('SNOW', 'Snowflake Inc.', 165.80, 162.40, 3.40, 2.09),
('SNPS', 'Synopsys Inc.', 565.40, 559.80, 5.60, 1.00),
('SPLK', 'Splunk Inc.', 152.80, 150.60, 2.20, 1.46),
('SWKS', 'Skyworks Solutions Inc.', 108.50, 106.80, 1.70, 1.59),
('TEAM', 'Atlassian Corp.', 225.60, 222.40, 3.20, 1.44),
('TTD', 'The Trade Desk Inc.', 85.40, 83.60, 1.80, 2.15),
('TXN', 'Texas Instruments Inc.', 175.80, 173.60, 2.20, 1.27),
('U', 'Unity Software Inc.', 28.50, 27.80, 0.70, 2.52),
('WDAY', 'Workday Inc.', 268.40, 265.20, 3.20, 1.21),
('ZM', 'Zoom Video Communications', 68.50, 67.20, 1.30, 1.93),
('ZS', 'Zscaler Inc.', 205.80, 202.40, 3.40, 1.68),

('ABBV', 'AbbVie Inc.', 178.50, 176.80, 1.70, 0.96),
('ABT', 'Abbott Laboratories', 112.40, 111.20, 1.20, 1.08),
('CVS', 'CVS Health Corp.', 78.50, 77.80, 0.70, 0.90),
('DHR', 'Danaher Corp.', 258.60, 255.80, 2.80, 1.09),
('JNJ', 'Johnson & Johnson', 158.30, 159.50, -1.20, -0.75),
('LLY', 'Eli Lilly and Co.', 785.60, 778.40, 7.20, 0.93),
('MRK', 'Merck & Co. Inc.', 128.50, 127.20, 1.30, 1.02),
('PFE', 'Pfizer Inc.', 28.40, 28.10, 0.30, 1.07),
('TMO', 'Thermo Fisher Scientific', 585.40, 579.80, 5.60, 0.97),
('UNH', 'UnitedHealth Group Inc.', 525.80, 520.40, 5.40, 1.04),
('VEEV', 'Veeva Systems Inc.', 198.50, 195.80, 2.70, 1.38),

('AXP', 'American Express Co.', 225.80, 223.40, 2.40, 1.07),
('BAC', 'Bank of America Corp.', 35.80, 35.40, 0.40, 1.13),
('BLK', 'BlackRock Inc.', 825.60, 818.40, 7.20, 0.88),
('BRK.B', 'Berkshire Hathaway Inc.', 385.40, 382.60, 2.80, 0.73),
('C', 'Citigroup Inc.', 58.50, 57.80, 0.70, 1.21),
('COIN', 'Coinbase Global Inc.', 185.40, 178.60, 6.80, 3.81),
('GS', 'Goldman Sachs Group Inc.', 425.80, 421.40, 4.40, 1.04),
('HOOD', 'Robinhood Markets Inc.', 12.80, 12.40, 0.40, 3.23),
('JPM', 'JPMorgan Chase & Co.', 198.40, 196.80, 1.60, 0.81),
('MA', 'Mastercard Inc.', 468.50, 464.20, 4.30, 0.93),
('MS', 'Morgan Stanley', 98.50, 97.40, 1.10, 1.13),
('SQ', 'Block Inc.', 78.50, 76.80, 1.70, 2.21),
('V', 'Visa Inc.', 285.60, 283.90, 1.70, 0.60),
('WFC', 'Wells Fargo & Co.', 52.80, 52.20, 0.60, 1.15),

('ABNB', 'Airbnb Inc.', 152.80, 149.60, 3.20, 2.14),
('AMZN', 'Amazon.com Inc.', 178.50, 176.80, 1.70, 0.96),
('DASH', 'DoorDash Inc.', 125.40, 122.80, 2.60, 2.12),
('DKNG', 'DraftKings Inc.', 42.50, 41.20, 1.30, 3.16),
('F', 'Ford Motor Co.', 12.50, 12.30, 0.20, 1.63),
('GM', 'General Motors Co.', 38.50, 38.00, 0.50, 1.32),
('HD', 'Home Depot Inc.', 365.80, 362.40, 3.40, 0.94),
('LCID', 'Lucid Group Inc.', 4.85, 4.68, 0.17, 3.63),
('LOW', 'Lowes Companies Inc.', 245.60, 242.80, 2.80, 1.15),
('LYFT', 'Lyft Inc.', 15.80, 15.40, 0.40, 2.60),
('MCD', 'McDonalds Corp.', 298.50, 296.20, 2.30, 0.78),
('NKE', 'Nike Inc.', 108.50, 107.20, 1.30, 1.21),
('RIVN', 'Rivian Automotive Inc.', 18.50, 17.80, 0.70, 3.93),
('SBUX', 'Starbucks Corp.', 98.50, 97.40, 1.10, 1.13),
('TGT', 'Target Corp.', 145.80, 143.60, 2.20, 1.53),
('TSLA', 'Tesla Inc.', 248.75, 252.30, -3.55, -1.41),
('UBER', 'Uber Technologies Inc.', 72.50, 71.20, 1.30, 1.83),

('COST', 'Costco Wholesale Corp.', 725.80, 718.60, 7.20, 1.00),
('KO', 'Coca-Cola Co.', 62.50, 62.10, 0.40, 0.64),
('PEP', 'PepsiCo Inc.', 175.80, 174.20, 1.60, 0.92),
('PG', 'Procter & Gamble Co.', 162.45, 161.80, 0.65, 0.40),
('WMT', 'Walmart Inc.', 165.80, 164.20, 1.60, 0.97),

('DIS', 'The Walt Disney Company', 112.30, 114.50, -2.20, -1.92),
('NFLX', 'Netflix Inc.', 628.90, 622.40, 6.50, 1.04),
('PINS', 'Pinterest Inc.', 38.50, 37.60, 0.90, 2.39),
('RBLX', 'Roblox Corp.', 42.50, 41.20, 1.30, 3.16),
('ROKU', 'Roku Inc.', 68.50, 66.80, 1.70, 2.54),
('SNAP', 'Snap Inc.', 12.80, 12.40, 0.40, 3.23),
('SPOT', 'Spotify Technology SA', 285.40, 280.60, 4.80, 1.71),
('T', 'AT&T Inc.', 18.50, 18.30, 0.20, 1.09),
('TMUS', 'T-Mobile US Inc.', 165.80, 163.60, 2.20, 1.34),
('VZ', 'Verizon Communications', 42.50, 42.10, 0.40, 0.95),

('BA', 'Boeing Co.', 215.80, 212.40, 3.40, 1.60),
('CAT', 'Caterpillar Inc.', 325.80, 322.40, 3.40, 1.05),
('GE', 'General Electric Co.', 158.50, 156.20, 2.30, 1.47),
('HON', 'Honeywell International', 205.80, 203.60, 2.20, 1.08),
('MMM', 'The 3M Co.', 98.50, 97.40, 1.10, 1.13),

('COP', 'ConocoPhillips', 118.50, 116.80, 1.70, 1.46),
('CVX', 'Chevron Corp.', 158.50, 156.80, 1.70, 1.08),
('EOG', 'EOG Resources Inc.', 128.50, 126.80, 1.70, 1.34),
('SLB', 'Schlumberger Ltd.', 52.50, 51.80, 0.70, 1.35),
('XOM', 'Exxon Mobil Corp.', 108.50, 107.20, 1.30, 1.21),

('ARKK', 'ARK Innovation ETF', 52.80, 51.40, 1.40, 2.72),
('DIA', 'SPDR Dow Jones Industrial', 398.50, 395.80, 2.70, 0.68),
('IWM', 'iShares Russell 2000 ETF', 208.50, 206.80, 1.70, 0.82),
('QQQ', 'Invesco QQQ Trust', 445.80, 442.40, 3.40, 0.77),
('SCHD', 'Schwab US Dividend Equity', 78.50, 77.80, 0.70, 0.90),
('SPY', 'SPDR S&P 500 ETF Trust', 512.80, 509.60, 3.20, 0.63),
('TLT', 'iShares 20+ Year Treasury', 98.50, 97.80, 0.70, 0.72),
('VIG', 'Vanguard Dividend Appreciation', 178.50, 177.20, 1.30, 0.73),
('VNQ', 'Vanguard Real Estate ETF', 92.50, 91.80, 0.70, 0.76),
('VOO', 'Vanguard S&P 500 ETF', 472.80, 469.60, 3.20, 0.68),
('VTI', 'Vanguard Total Stock Market', 258.50, 256.40, 2.10, 0.82),
('XLE', 'Energy Select Sector SPDR', 92.50, 91.40, 1.10, 1.20),
('XLF', 'Financial Select Sector SPDR', 42.50, 42.10, 0.40, 0.95),
('XLI', 'Industrial Select Sector SPDR', 118.50, 117.40, 1.10, 0.94),
('XLK', 'Technology Select Sector SPDR', 205.80, 203.60, 2.20, 1.08),
('XLV', 'Health Care Select Sector SPDR', 145.80, 144.60, 1.20, 0.83),

('GLD', 'SPDR Gold Shares', 218.50, 217.20, 1.30, 0.60),
('SLV', 'iShares Silver Trust', 25.80, 25.50, 0.30, 1.18),
('USO', 'United States Oil Fund', 78.50, 77.60, 0.90, 1.16)
ON DUPLICATE KEY UPDATE 
    company_name = VALUES(company_name),
    current_price = VALUES(current_price),
    previous_close = VALUES(previous_close),
    day_change = VALUES(day_change),
    day_change_percent = VALUES(day_change_percent);

CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
CREATE INDEX idx_alerts_user_read ON alerts(user_id, is_read);
CREATE INDEX idx_snapshots_user_date ON portfolio_snapshots(user_id, snapshot_date);

SELECT 'Database schema created successfully!' AS Status;
