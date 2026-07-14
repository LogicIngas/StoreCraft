import React, { useState } from 'react';

/**
 * Login/Register Component with Role Selection
 */
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
                <div className="auth-logo">🛍️</div>
                <h1 className="auth-title">Kasi Connect</h1>
                <p className="auth-subtitle">{isLogin ? 'Welcome back!' : 'Create your account'}</p>

                <form onSubmit={handleSubmit} className="auth-form">
                    <input
                        type="email"
                        placeholder="📧 Email Address"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="auth-input"
                        required
                    />

                    <div className="password-input-wrapper">
                        <input
                            type={showPassword ? 'text' : 'password'}
                            placeholder="🔒 Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="auth-input"
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
                                    type="text"
                                    placeholder="First Name"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                    className="auth-input auth-name-input"
                                />
                                <input
                                    type="text"
                                    placeholder="Last Name"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                    className="auth-input auth-name-input"
                                />
                            </div>

                            <div className="auth-role-selection">
                                <label className="auth-role-label">Select Account Type:</label>
                                <div className="auth-role-options">
                                    <label className="auth-role-option">
                                        <input
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