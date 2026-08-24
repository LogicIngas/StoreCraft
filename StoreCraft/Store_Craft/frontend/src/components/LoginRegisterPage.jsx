import React, { useState } from 'react';
import Logo1 from '../assets/Logo1.jpg';

export default function LoginRegisterPage({ onLogin, onRegister }) {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [selectedRole, setSelectedRole] = useState('BUYER');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isLogin) {
      onLogin(email, password);
    } else {
      onRegister(email, password, firstName, lastName, selectedRole);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <img src={Logo1} alt="StoreCraft Logo" className="auth-logo" />
        <h1 className="auth-title">StoreCraft</h1>
        <p className="auth-subtitle">{isLogin ? 'Welcome back!' : 'Create your account'}</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            id="auth-email"
            name="email"
            type="email"
            placeholder="📧 Email Address"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="auth-input"
            autoComplete="email"
            required
          />

          <div className="password-input-wrapper">
            <input
              id="auth-password"
              name="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="🔒 Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="auth-input"
              autoComplete={isLogin ? 'current-password' : 'new-password'}
              required
            />
            <button
              type="button"
              className="password-toggle"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? '🙈' : '👁️'}
            </button>
          </div>

          {!isLogin && (
            <>
              <div className="auth-name-row">
                <input
                  id="auth-firstName"
                  name="firstName"
                  type="text"
                  placeholder="First Name"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  className="auth-input auth-name-input"
                  autoComplete="given-name"
                />
                <input
                  id="auth-lastName"
                  name="lastName"
                  type="text"
                  placeholder="Last Name"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  className="auth-input auth-name-input"
                  autoComplete="family-name"
                />
              </div>

              <div className="auth-role-selection">
                <label className="auth-role-label">Select Account Type:</label>
                <div className="auth-role-options">
                  <label className="auth-role-option">
                    <input
                      id="role-buyer"
                      type="radio"
                      name="role"
                      value="BUYER"
                      checked={selectedRole === 'BUYER'}
                      onChange={(e) => setSelectedRole(e.target.value)}
                    />
                    <span className="role-option-label">
                      <span className="role-icon">🛒</span>
                      Buyer
                      <span className="role-description">Browse & purchase products</span>
                    </span>
                  </label>
                  <label className="auth-role-option">
                    <input
                      id="role-seller"
                      type="radio"
                      name="role"
                      value="SELLER"
                      checked={selectedRole === 'SELLER'}
                      onChange={(e) => setSelectedRole(e.target.value)}
                    />
                    <span className="role-option-label">
                      <span className="role-icon">📤</span>
                      Seller
                      <span className="role-description">Upload & sell products</span>
                    </span>
                  </label>
                </div>
              </div>
            </>
          )}

          <button type="submit" className="auth-button">
            {isLogin ? 'Sign In' : 'Create Account'}
          </button>

          <button
            type="button"
            onClick={() => setIsLogin(!isLogin)}
            className="auth-toggle"
          >
            {isLogin ? "Don't have an account? Register" : "Already have an account? Login"}
          </button>
        </form>
      </div>
    </div>
  );
}