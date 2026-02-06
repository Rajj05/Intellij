import api from './config';

// Stock endpoints
export const getAllStocks = async () => {
  const response = await api.get('/stocks');
  return response;
};

export const getStock = async (ticker) => {
  const response = await api.get(`/stocks/${ticker}`);
  return response;
};

export const getStockQuote = async (ticker) => {
  const response = await api.get(`/stocks/${ticker}/quote`);
  return response;
};

export const refreshStockPrice = async (ticker) => {
  const response = await api.post(`/stocks/${ticker}/refresh`);
  return response;
};

export const refreshAllStocks = async () => {
  const response = await api.post('/stocks/refresh-all');
  return response;
};

export const getHistoricalData = async (ticker, period = '1M') => {
  const response = await api.get(`/stocks/${ticker}/history?period=${period}`);
  return response;
};

// Portfolio endpoints
export const getPortfolioSummary = async (userId) => {
  const response = await api.get(`/portfolio/${userId}/summary`);
  return response;
};

export const getUserHoldings = async (userId) => {
  const response = await api.get(`/portfolio/${userId}/holdings`);
  return response;
};

export const getTransactionHistory = async (userId) => {
  const response = await api.get(`/portfolio/${userId}/transactions`);
  return response;
};

// Transaction endpoints
export const buyStock = async (data) => {
  const response = await api.post('/transaction/buy', data);
  return response;
};

export const sellStock = async (data) => {
  const response = await api.post('/transaction/sell', data);
  return response;
};

// User endpoints
export const getUser = async (userId) => {
  const response = await api.get(`/user/${userId}`);
  return response;
};

export const updateWalletBalance = async (userId, amount) => {
  const response = await api.get(`/user/${userId}/wallet/${amount}`);
  return response;
};

// Alert endpoints
export const getUserAlerts = async (userId) => {
  const response = await api.get(`/alerts/${userId}`);
  return response;
};

export const createAlert = async (data) => {
  const response = await api.post('/alerts', data);
  return response;
};

// Notification endpoints
export const getUnreadNotifications = async (userId) => {
  const response = await api.get(`/notifications/${userId}/unread`);
  return response;
};

export const getAllNotifications = async (userId) => {
  const response = await api.get(`/notifications/${userId}`);
  return response;
};

export const markNotificationAsRead = async (notificationId) => {
  const response = await api.put(`/notifications/${notificationId}/read`);
  return response;
};