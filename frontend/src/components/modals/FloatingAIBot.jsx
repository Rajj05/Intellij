import React, { useState, useRef, useEffect } from 'react';
import { Bot, X, Send, Lock, AlertTriangle } from 'lucide-react';
import { getAiPasswordStatus, setAiPassword, verifyAiPassword, aiBuyStock, aiSellStock, getStockPrice } from '../../api/stockApi';
import './FloatingAIBot.css';

const FloatingAIBot = ({ userId }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [hasPassword, setHasPassword] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [pendingTrade, setPendingTrade] = useState(null);
  const [awaitingPassword, setAwaitingPassword] = useState(false);
  const [awaitingNewPassword, setAwaitingNewPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    if (isOpen && messages.length === 0) {
      checkPasswordStatus();
    }
  }, [isOpen]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const addBotMessage = (text) => {
    setMessages(prev => [...prev, { role: 'bot', text }]);
  };

  const addUserMessage = (text) => {
    setMessages(prev => [...prev, { role: 'user', text }]);
  };

  const checkPasswordStatus = async () => {
    try {
      const res = await getAiPasswordStatus(userId);
      const has = res.data.hasPassword;
      setHasPassword(has);
      if (!has) {
        addBotMessage("Welcome! I'm your AI Trading Assistant. Before we begin, please set a trade password that I'll ask for before executing any trades.");
        addBotMessage("Please type your new trade password:");
        setAwaitingNewPassword(true);
      } else {
        addBotMessage("Welcome back! I'm your AI Trading Assistant. I can help you buy or sell stocks. Try saying something like:\n\n• \"Buy 10 shares of AAPL\"\n• \"Sell 5 shares of TSLA\"\n• \"What's the price of MSFT?\"");
      }
    } catch {
      addBotMessage("Sorry, I couldn't connect to the server. Please try again later.");
    }
  };

  const parseTrade = (text) => {
    const buyMatch = text.match(/buy\s+(\d+)\s+(?:shares?\s+(?:of\s+)?)?([A-Za-z]{1,5})/i);
    if (buyMatch) {
      return { action: 'BUY', quantity: parseInt(buyMatch[1]), ticker: buyMatch[2].toUpperCase() };
    }
    const sellMatch = text.match(/sell\s+(\d+)\s+(?:shares?\s+(?:of\s+)?)?([A-Za-z]{1,5})/i);
    if (sellMatch) {
      return { action: 'SELL', quantity: parseInt(sellMatch[1]), ticker: sellMatch[2].toUpperCase() };
    }
    const priceMatch = text.match(/(?:price|quote|check)\s+(?:of\s+|for\s+)?([A-Za-z]{1,5})/i);
    if (priceMatch) {
      return { action: 'PRICE', ticker: priceMatch[1].toUpperCase() };
    }
    return null;
  };

  const executeTrade = async (trade, password) => {
    setIsLoading(true);
    try {
      const verifyRes = await verifyAiPassword(userId, password);
      if (!verifyRes.data.valid) {
        addBotMessage("❌ Incorrect password. Trade cancelled.");
        setPendingTrade(null);
        setAwaitingPassword(false);
        setIsLoading(false);
        return;
      }

      setIsAuthenticated(true);
      const payload = {
        userId,
        ticker: trade.ticker,
        quantity: trade.quantity,
        password
      };

      if (trade.action === 'BUY') {
        const res = await aiBuyStock(payload);
        addBotMessage(`✅ Successfully bought ${trade.quantity} shares of ${trade.ticker} at $${res.data.price?.toFixed(2) || 'market price'}. Total: $${res.data.totalCost?.toFixed(2) || 'N/A'}`);
      } else {
        const res = await aiSellStock(payload);
        addBotMessage(`✅ Successfully sold ${trade.quantity} shares of ${trade.ticker} at $${res.data.price?.toFixed(2) || 'market price'}. Total: $${res.data.totalRevenue?.toFixed(2) || 'N/A'}`);
      }
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data || 'Something went wrong';
      addBotMessage(`❌ Trade failed: ${msg}`);
    }
    setPendingTrade(null);
    setAwaitingPassword(false);
    setIsLoading(false);
  };

  const handleSend = async () => {
    const text = input.trim();
    if (!text || isLoading) return;
    addUserMessage(text);
    setInput('');

    if (awaitingNewPassword) {
      if (text.length < 4) {
        addBotMessage("Password must be at least 4 characters. Please try again:");
        return;
      }
      try {
        await setAiPassword(userId, text);
        setHasPassword(true);
        setAwaitingNewPassword(false);
        addBotMessage("✅ Trade password set! You can now ask me to trade. Try:\n\n• \"Buy 10 shares of AAPL\"\n• \"Sell 5 shares of TSLA\"");
      } catch {
        addBotMessage("Failed to set password. Please try again:");
      }
      return;
    }

    if (awaitingPassword && pendingTrade) {
      await executeTrade(pendingTrade, text);
      return;
    }

    const trade = parseTrade(text);
    if (!trade) {
      if (text.toLowerCase().includes('help')) {
        addBotMessage("I can help you trade stocks! Try:\n\n• \"Buy 10 shares of AAPL\"\n• \"Sell 5 shares of TSLA\"\n• \"Price of MSFT\"\n• \"Change password\"");
      } else if (text.toLowerCase().includes('change password') || text.toLowerCase().includes('reset password')) {
        addBotMessage("Please type your new trade password:");
        setAwaitingNewPassword(true);
      } else {
        addBotMessage("I didn't understand that. Try something like \"Buy 10 shares of AAPL\" or type \"help\" for options.");
      }
      return;
    }

    if (trade.action === 'PRICE') {
      setIsLoading(true);
      try {
        const res = await getStockPrice(trade.ticker);
        const price = res.data?.c || res.data?.currentPrice;
        if (price) {
          addBotMessage(`📈 ${trade.ticker} is currently trading at $${price.toFixed(2)}`);
        } else {
          addBotMessage(`Could not find price for ${trade.ticker}.`);
        }
      } catch {
        addBotMessage(`Failed to get price for ${trade.ticker}.`);
      }
      setIsLoading(false);
      return;
    }

    addBotMessage(`🔐 To ${trade.action.toLowerCase()} ${trade.quantity} shares of ${trade.ticker}, please enter your trade password:`);
    setPendingTrade(trade);
    setAwaitingPassword(true);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="ai-bot-container">
      {isOpen && (
        <div className="ai-bot-chat">
          <div className="ai-bot-header">
            <div className="ai-bot-header-title">
              <Bot size={20} />
              <span>AI Trading Assistant</span>
            </div>
            <button className="ai-bot-close" onClick={() => setIsOpen(false)}>
              <X size={18} />
            </button>
          </div>
          <div className="ai-bot-messages">
            {messages.map((msg, i) => (
              <div key={i} className={`ai-bot-msg ${msg.role}`}>
                {msg.role === 'bot' && <Bot size={16} className="ai-bot-msg-icon" />}
                <div className="ai-bot-msg-text">{msg.text}</div>
              </div>
            ))}
            {isLoading && (
              <div className="ai-bot-msg bot">
                <Bot size={16} className="ai-bot-msg-icon" />
                <div className="ai-bot-msg-text ai-bot-typing">
                  <span></span><span></span><span></span>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
          <div className="ai-bot-input-area">
            {awaitingPassword && <Lock size={14} className="ai-bot-lock-icon" />}
            <input
              type={awaitingPassword || awaitingNewPassword ? 'password' : 'text'}
              className="ai-bot-input"
              placeholder={awaitingPassword ? 'Enter trade password...' : awaitingNewPassword ? 'Set your trade password...' : 'Ask me to trade...'}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={isLoading}
            />
            <button className="ai-bot-send" onClick={handleSend} disabled={isLoading || !input.trim()}>
              <Send size={16} />
            </button>
          </div>
        </div>
      )}
      <button className={`ai-bot-fab ${isOpen ? 'open' : ''}`} onClick={() => setIsOpen(!isOpen)}>
        {isOpen ? <X size={24} /> : <Bot size={24} />}
      </button>
    </div>
  );
};

export default FloatingAIBot;
