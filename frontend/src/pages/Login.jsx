import React, { useState } from 'react';
import { TrendingUp, Eye, EyeOff, ArrowRight, UserPlus, LogIn } from 'lucide-react';
import { loginUser, registerUser } from '../api/stockApi';
import './Login.css';

const Login = ({ onLogin }) => {
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let response;
      if (isRegister) {
        if (!username || !email || !password) {
          setError('All fields are required');
          setLoading(false);
          return;
        }
        response = await registerUser({ username, email, password });
      } else {
        if (!username || !password) {
          setError('Username and password are required');
          setLoading(false);
          return;
        }
        response = await loginUser({ username, password });
      }

      if (response.success) {
        onLogin(response.data);
      } else {
        setError(response.message || 'Something went wrong');
      }
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Network error';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const toggleMode = () => {
    setIsRegister(!isRegister);
    setError('');
  };

  return (
    <div className="login-page">
      <div className="login-bg-effects">
        <div className="login-orb login-orb-1" />
        <div className="login-orb login-orb-2" />
        <div className="login-orb login-orb-3" />
      </div>

      <div className="login-container">
        <div className="login-brand">
          <div className="login-brand-icon">
            <TrendingUp size={32} />
          </div>
          <h1>IntelliJX</h1>
          <p>Portfolio Manager</p>
        </div>

        <div className="login-card">
          <div className="login-card-header">
            <h2>{isRegister ? 'Create Account' : 'Welcome Back'}</h2>
            <p>{isRegister ? 'Start your investment journey' : 'Sign in to your portfolio'}</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter your username"
                autoComplete="username"
              />
            </div>

            {isRegister && (
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Enter your email"
                  autoComplete="email"
                />
              </div>
            )}

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <div className="password-input">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  autoComplete={isRegister ? 'new-password' : 'current-password'}
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {error && <div className="login-error">{error}</div>}

            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? (
                <span className="login-spinner" />
              ) : (
                <>
                  {isRegister ? <UserPlus size={18} /> : <LogIn size={18} />}
                  <span>{isRegister ? 'Create Account' : 'Sign In'}</span>
                  <ArrowRight size={18} />
                </>
              )}
            </button>
          </form>

          <div className="login-footer">
            <span>{isRegister ? 'Already have an account?' : "Don't have an account?"}</span>
            <button type="button" className="login-toggle" onClick={toggleMode}>
              {isRegister ? 'Sign In' : 'Create Account'}
            </button>
          </div>
        </div>

        <p className="login-disclaimer">
          Paper trading simulator — no real money involved
        </p>
      </div>
    </div>
  );
};

export default Login;
