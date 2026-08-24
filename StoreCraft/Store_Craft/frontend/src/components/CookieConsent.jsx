import React, { useState, useEffect } from 'react';
import './CookieConsent.css';

const COOKIE_CONSENT_KEY = 'cookieConsentAccepted';

export default function CookieConsent({ onAccept, onDecline }) {
  const [show, setShow] = useState(false);

  useEffect(() => {
    const consent = localStorage.getItem(COOKIE_CONSENT_KEY);
    if (!consent) {
      setShow(true);
    }
  }, []);

  const handleAccept = () => {
    localStorage.setItem(COOKIE_CONSENT_KEY, 'accepted');
    setShow(false);
    if (onAccept) onAccept();
  };

  const handleDecline = () => {
    localStorage.setItem(COOKIE_CONSENT_KEY, 'declined');
    setShow(false);
    if (onDecline) onDecline();
  };

  if (!show) return null;

  return (
    <div className="cookie-overlay">
      <div className="cookie-popup">
        <div className="cookie-popup-icon">🍪</div>
        <h2 className="cookie-popup-title">We Value Your Privacy</h2>
        <p className="cookie-popup-text">
          StoreCraft uses cookies to keep you logged in, remember your preferences,
          and improve your shopping experience. By clicking <strong>"Accept All"</strong> you
          consent to the use of cookies. You can decline, but some features may not
          work as expected.
        </p>
        <div className="cookie-popup-details">
          <div className="cookie-type">
            <span className="cookie-type-icon">🔒</span>
            <div>
              <strong>Essential Cookies</strong>
              <p>Required for login and security — always active.</p>
            </div>
          </div>
          <div className="cookie-type">
            <span className="cookie-type-icon">📊</span>
            <div>
              <strong>Analytics Cookies</strong>
              <p>Help us understand how you use our site.</p>
            </div>
          </div>
          <div className="cookie-type">
            <span className="cookie-type-icon">🎯</span>
            <div>
              <strong>Preference Cookies</strong>
              <p>Remember your settings and personalize your experience.</p>
            </div>
          </div>
        </div>
        <div className="cookie-popup-actions">
          <button className="cookie-btn-accept" onClick={handleAccept}>
            ✅ Accept All
          </button>
          <button className="cookie-btn-decline" onClick={handleDecline}>
            Decline
          </button>
        </div>
        <p className="cookie-popup-policy">
          By using StoreCraft, you agree to our{' '}
          <a href="#privacy">Privacy Policy</a> and{' '}
          <a href="#cookies">Cookie Policy</a>.
        </p>
      </div>
    </div>
  );
}