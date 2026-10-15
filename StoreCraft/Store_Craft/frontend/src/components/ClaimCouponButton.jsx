/**
 * ClaimCouponButton.jsx
 * A button that opens a modal for the visitor to enter their email,
 * calls the `claim-coupon` Edge Function, and shows the outcome.
 * Works for both logged-in and anonymous visitors.
 */

import React, { useState, useEffect, useRef } from 'react';
import { supabase } from '../supabaseClient';
import './ClaimCouponButton.css';

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

export default function ClaimCouponButton({ product, currentUser }) {
  const [open, setOpen] = useState(false);
  const [email, setEmail] = useState(currentUser?.email || '');
  const [status, setStatus] = useState('idle'); // idle | loading | success | error
  const [message, setMessage] = useState('');
  const inputRef = useRef(null);

  // Pre-fill email when user logs in mid-session
  useEffect(() => {
    if (currentUser?.email && !email) {
      setEmail(currentUser.email);
    }
  }, [currentUser]);

  // Focus email input when modal opens
  useEffect(() => {
    if (open && inputRef.current) {
      setTimeout(() => inputRef.current?.focus(), 60);
    }
  }, [open]);

  // Close on Escape
  useEffect(() => {
    if (!open) return;
    const handleKey = (e) => {
      if (e.key === 'Escape') handleClose();
    };
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [open]);

  const handleClose = () => {
    setOpen(false);
    setStatus('idle');
    setMessage('');
    if (!currentUser?.email) setEmail('');
  };

  const handleClaim = async (e) => {
    e.preventDefault();

    if (!isValidEmail(email)) {
      setStatus('error');
      setMessage('Please enter a valid email address.');
      return;
    }

    setStatus('loading');
    setMessage('');

    try {
      const { data, error } = await supabase.functions.invoke('claim-coupon', {
        body: {
          product_id: product.productId,
          email: email.trim().toLowerCase(),
          user_id: currentUser?.userId || null,
        },
      });

      if (error || !data?.ok) {
        setStatus('error');
        setMessage(data?.error || error?.message || 'Failed to claim coupon.');
        return;
      }

      setStatus('success');
      setMessage(
        data.warning
          ? `Coupon saved! Note: ${data.warning}`
          : 'Check your email for your exclusive coupon code! 🎉'
      );
    } catch (err) {
      console.error('claim-coupon error:', err);
      setStatus('error');
      setMessage('Something went wrong. Please try again.');
    }
  };

  return (
    <>
      <button
        type="button"
        id={`claim-coupon-btn-${product.productId}`}
        className="ccb-trigger"
        onClick={() => setOpen(true)}
        aria-haspopup="dialog"
      >
        🏷️ Claim Coupon
      </button>

      {open && (
        <div
          className="ccb-overlay"
          role="dialog"
          aria-modal="true"
          aria-labelledby="ccb-modal-title"
          onClick={(e) => e.target === e.currentTarget && handleClose()}
        >
          <div className="ccb-modal">
            {/* Header */}
            <div className="ccb-modal-header">
              <div>
                <h2 id="ccb-modal-title" className="ccb-modal-title">
                  🏷️ Claim Your Coupon
                </h2>
                <p className="ccb-modal-subtitle">
                  Get <strong>10% off</strong>{' '}
                  <em>{product.name}</em>
                </p>
              </div>
              <button
                type="button"
                className="ccb-close"
                onClick={handleClose}
                aria-label="Close dialog"
              >
                ✕
              </button>
            </div>

            {/* Body */}
            <div className="ccb-modal-body">
              {status === 'success' ? (
                <div className="ccb-success-state">
                  <div className="ccb-success-icon">✉️</div>
                  <h3>Check Your Email!</h3>
                  <p>{message}</p>
                  <p className="ccb-success-hint">
                    Your coupon code has been sent to <strong>{email}</strong>.
                    Show it in-store or mention it on delivery.
                  </p>
                  <button
                    type="button"
                    className="ccb-done-btn"
                    onClick={handleClose}
                  >
                    Done
                  </button>
                </div>
              ) : (
                <form onSubmit={handleClaim} className="ccb-form" noValidate>
                  <div className="ccb-product-pill">
                    <span>📦</span>
                    <span>{product.name}</span>
                    <span className="ccb-discount-badge">-10%</span>
                  </div>

                  {status === 'error' && (
                    <div className="ccb-error-msg" role="alert">
                      ❌ {message}
                    </div>
                  )}

                  <div className="ccb-field">
                    <label htmlFor="ccb-email" className="ccb-label">
                      Email address
                    </label>
                    <input
                      ref={inputRef}
                      id="ccb-email"
                      type="email"
                      className="ccb-input"
                      placeholder="you@example.com"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                      autoComplete="email"
                      disabled={status === 'loading'}
                    />
                    <span className="ccb-field-hint">
                      We'll send your unique code to this address
                    </span>
                  </div>

                  <button
                    type="submit"
                    id="ccb-submit-btn"
                    className="ccb-submit-btn"
                    disabled={status === 'loading'}
                  >
                    {status === 'loading' ? (
                      <>
                        <span className="ccb-loader" /> Sending coupon…
                      </>
                    ) : (
                      '📨 Send My Coupon'
                    )}
                  </button>

                  <p className="ccb-terms">
                    Single-use code · Valid 30 days · Redeemable in-store or on delivery
                  </p>
                </form>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
