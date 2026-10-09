import React, { useState, useEffect } from 'react';
import Logo1 from '../assets/Logo1.jpg';
import { supabase } from '../supabaseClient';
import { apiUrl } from '../api.js';

export default function LoginRegisterPage({ onLogin, onRegister, initialResetToken, onResetDone }) {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [selectedRole, setSelectedRole] = useState('BUYER');

  // Forgot / Reset password state
  // If a reset token was passed from App (via the email link), start in 'reset' view
  const [view, setView] = useState(initialResetToken ? 'reset' : 'auth');
  const [forgotEmail, setForgotEmail] = useState('');
  const [forgotMsg, setForgotMsg] = useState('');
  const [resetToken, setResetToken] = useState(initialResetToken || '');
  const [newPassword, setNewPassword] = useState('');
  const [resetMsg, setResetMsg] = useState('');
  const [loading, setLoading] = useState(false);

  // If App.jsx passes a new token after mount, pick it up
  useEffect(() => {
    if (initialResetToken) {
      setResetToken(initialResetToken);
      setView('reset');
    }
  }, [initialResetToken]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isLogin) {
      onLogin(email, password);
    } else {
      onRegister(email, password, firstName, lastName, selectedRole);
    }
  };

  const handleForgotPassword = async (e) => {
    e.preventDefault();
    setLoading(true);
    setForgotMsg('');
    try {
      const res = await fetch(apiUrl('/user/forgot-password'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: forgotEmail }),
      });
      const data = await res.json();
      setForgotMsg(data.message || 'Reset link sent!');
    } catch {
      setForgotMsg('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    if (newPassword.length < 6) {
      setResetMsg('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    setResetMsg('');
    try {
      const res = await fetch(apiUrl('/user/reset-password'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: resetToken, newPassword }),
      });
      const data = await res.json();
      setResetMsg(data.message || 'Done!');
      if (res.ok) {
        if (onResetDone) onResetDone();
        setTimeout(() => setView('auth'), 2500);
      }
    } catch {
      setResetMsg('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSupabaseOAuth = async () => {
    try {
      const { error } = await supabase.auth.signInWithOAuth({ 
        provider: 'google', 
        options: { redirectTo: window.location.origin } 
      });
      if (error) throw error;
    } catch (error) {
      console.error('Error logging in with Google:', error.message);
      alert(error.message);
    }
  };

  // ── Reset Password View ──────────────────────────────────────────────
  if (view === 'reset') {
    return (
      <div className="auth-container">
        <div className="auth-card">
          <img src={Logo1} alt="StoreCraft Logo" className="auth-logo" />
          <h1 className="auth-title">Set New Password</h1>
          <p className="auth-subtitle">Choose a strong new password for your account.</p>
          <form onSubmit={handleResetPassword} className="auth-form">
            <div className="password-input-wrapper">
              <input
                type={showPassword ? 'text' : 'password'}
                placeholder="🔒 New Password (min 6 chars)"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="auth-input"
                required
                minLength={6}
              />
              <button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)}>
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
            {resetMsg && (
              <p style={{ color: resetMsg.includes('successfully') ? '#00897b' : '#e53e3e', fontSize: '14px', textAlign: 'center' }}>
                {resetMsg}
              </p>
            )}
            <button type="submit" className="auth-button" disabled={loading}>
              {loading ? 'Updating…' : 'Update Password'}
            </button>
          </form>
          <button type="button" onClick={() => setView('auth')} className="auth-toggle">
            ← Back to Sign In
          </button>
        </div>
      </div>
    );
  }

  // ── Forgot Password View ─────────────────────────────────────────────
  if (view === 'forgot') {
    return (
      <div className="auth-container">
        <div className="auth-card">
          <img src={Logo1} alt="StoreCraft Logo" className="auth-logo" />
          <h1 className="auth-title">Forgot Password?</h1>
          <p className="auth-subtitle">Enter your email and we'll send you a reset link.</p>
          <form onSubmit={handleForgotPassword} className="auth-form">
            <input
              type="email"
              placeholder="📧 Your Email Address"
              value={forgotEmail}
              onChange={(e) => setForgotEmail(e.target.value)}
              className="auth-input"
              required
            />
            {forgotMsg && (
              <p style={{ color: '#00897b', fontSize: '14px', textAlign: 'center', padding: '8px', background: '#e6fffa', borderRadius: '8px' }}>
                {forgotMsg}
              </p>
            )}
            <button type="submit" className="auth-button" disabled={loading}>
              {loading ? 'Sending…' : 'Send Reset Link'}
            </button>
          </form>
          <button type="button" onClick={() => setView('auth')} className="auth-toggle">
            ← Back to Sign In
          </button>
        </div>
      </div>
    );
  }

  // ── Main Auth View ───────────────────────────────────────────────────
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

          {isLogin && (
            <div style={{ textAlign: 'right', marginTop: '-4px', marginBottom: '4px' }}>
              <button
                type="button"
                onClick={() => setView('forgot')}
                style={{ background: 'none', border: 'none', color: '#00897b', fontSize: '13px', cursor: 'pointer', textDecoration: 'underline', padding: 0 }}
              >
                Forgot password?
              </button>
            </div>
          )}

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
                      <span className="role-description">Browse &amp; purchase products</span>
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
                      <span className="role-description">Upload &amp; sell products</span>
                    </span>
                  </label>
                </div>
              </div>
            </>
          )}

          <button type="submit" className="auth-button">
            {isLogin ? 'Sign In' : 'Create Account'}
          </button>

          <div className="supabase-auth-options" style={{ marginTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <div style={{ textAlign: 'center', margin: '0.5rem 0', color: '#666' }}>OR</div>
            <button
              type="button"
              onClick={handleSupabaseOAuth}
              className="auth-button"
              style={{ backgroundColor: '#4285F4', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px', padding: '10px' }}
            >
              <span style={{ backgroundColor: 'white', borderRadius: '50%', padding: '4px', display: 'flex', alignItems: 'center', justifyContent: 'center', width: '24px', height: '24px' }}>
                <svg width="16" height="16" viewBox="0 0 48 48">
                  <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.7 17.74 9.5 24 9.5z"/>
                  <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
                  <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
                  <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
                  <path fill="none" d="M0 0h48v48H0z"/>
                </svg>
              </span>
              Continue with Google
            </button>
          </div>

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