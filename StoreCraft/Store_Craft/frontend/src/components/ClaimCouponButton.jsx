/**
 * ClaimCouponButton.jsx
 * A button that opens a modal for the visitor to enter their email,
 * calls the `claim-coupon` Edge Function, and shows the outcome.
 * Works for both logged-in and anonymous visitors.
 */

import React, { useState, useEffect, useRef } from 'react';
import { supabase } from '../supabaseClient';
import { apiUrl } from '../api';
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

  const [claimedCode, setClaimedCode] = useState('');

  const randomSuffix = (len = 6) => {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    let res = '';
    for (let i = 0; i < len; i++) {
      res += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return res;
  };

  const handleClose = () => {
    setOpen(false);
    setStatus('idle');
    setMessage('');
    setClaimedCode('');
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
      let claimed = false;
      let codeObtained = null;
      const cleanEmail = email.trim().toLowerCase();

      // 1. Try Supabase Edge Function first
      try {
        const { data, error } = await supabase.functions.invoke('claim-coupon', {
          body: {
            product_id: product.productId,
            email: cleanEmail,
            user_id: currentUser?.userId || null,
          },
        });

        if (!error && data?.ok) {
          claimed = true;
          codeObtained = data.code;
        } else if (data?.error) {
          setStatus('error');
          setMessage(data.error);
          return;
        }
      } catch (edgeErr) {
        console.warn('Edge function unavailable, using direct fallback:', edgeErr);
      }

      // 2. Direct database fallback if Edge Function is not deployed
      if (!claimed) {
        // Check for duplicate claim
        const { data: existing } = await supabase
          .from('product_coupons')
          .select('claim_id')
          .eq('product_id', product.productId)
          .eq('email', cleanEmail)
          .maybeSingle();

        if (existing) {
          setStatus('error');
          setMessage('You have already claimed a coupon for this product with this email.');
          return;
        }

        codeObtained = 'SAVE-' + randomSuffix();
        const couponId = crypto.randomUUID();
        const now = new Date();
        const nowIso = now.toISOString();
        const validToIso = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString();

        // Insert into coupons table (with created_at and updated_at explicitly populated)
        const { error: cErr } = await supabase.from('coupons').insert({
          coupon_id: couponId,
          code: codeObtained,
          description: `10% off ${product.name}`,
          discount_type: 'PERCENTAGE',
          discount_value: 10,
          is_active: true,
          usage_count: 0,
          usage_limit: 1,
          valid_from: nowIso,
          valid_to: validToIso,
          created_at: nowIso,
          updated_at: nowIso,
        });
        if (cErr) throw cErr;

        // Insert into product_coupons table
        const { error: pcErr } = await supabase.from('product_coupons').insert({
          claim_id: crypto.randomUUID(),
          product_id: product.productId,
          coupon_id: couponId,
          user_id: currentUser?.userId || null,
          email: cleanEmail,
          status: 'claimed',
          claimed_at: nowIso,
        });
        if (pcErr) throw pcErr;

        // Insert into notifications table
        if (currentUser?.userId) {
          try {
            await supabase.from('notifications').insert({
              notification_id: crypto.randomUUID(),
              user_id: currentUser.userId,
              title: '🏷️ 10% Coupon Claimed!',
              message: `You claimed a 10% discount coupon (${codeObtained}) for ${product.name}. Valid for 30 days!`,
              type: 'COUPON',
              link: `/product/${product.productId}`,
              is_read: false,
              created_at: nowIso,
            });
          } catch (notifErr) {
            console.warn('Failed to insert notification record (non-fatal):', notifErr);
          }
        }
      }

      // 3. ALWAYS send confirmation email (via Spring Boot backend using Render BREVO env vars, with direct Brevo fallback)
      if (codeObtained) {
        let sentViaBackend = false;
        try {
          const res = await fetch(apiUrl('/coupon/send-claim-email'), {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              email: cleanEmail,
              couponCode: codeObtained,
              productName: product.name,
            }),
          });
          if (res.ok) {
            sentViaBackend = true;
          }
        } catch (beErr) {
          console.warn('Backend coupon email dispatch attempt failed, trying fallback:', beErr);
        }

        // Direct Brevo API fallback if backend call did not succeed or if direct key is available
        const brevoKey = import.meta.env.VITE_BREVO_API_KEY;
        if (!sentViaBackend && brevoKey) {
          try {
            const res = await fetch('https://api.brevo.com/v3/smtp/email', {
              method: 'POST',
              headers: {
                'api-key': brevoKey,
                'Content-Type': 'application/json',
              },
              body: JSON.stringify({
                sender: { name: 'StoreCraft', email: 'mboboinga12@gmail.com' },
                to: [{ email: cleanEmail }],
                subject: `🏷️ Your 10% Coupon for ${product.name} [${codeObtained}]`,
                htmlContent: `<div style="font-family:Segoe UI,Roboto,sans-serif;max-width:580px;margin:20px auto;padding:32px;border:1px solid #e2e8f0;border-radius:16px;background:#ffffff;box-shadow:0 4px 12px rgba(0,0,0,0.05);">
                  <div style="text-align:center;margin-bottom:24px;">
                    <span style="font-size:40px;">🏷️</span>
                    <h1 style="color:#0f766e;font-size:24px;margin:8px 0;">Here is Your 10% Discount Coupon!</h1>
                    <p style="color:#64748b;font-size:15px;margin:0;">Thank you for shopping with StoreCraft.</p>
                  </div>
                  <div style="background:#f8fafc;border-radius:12px;padding:20px;margin:20px 0;border:1px solid #e2e8f0;">
                    <div style="font-size:14px;color:#64748b;margin-bottom:4px;">Product Discount:</div>
                    <div style="font-size:18px;font-weight:600;color:#0f172a;">${product.name}</div>
                  </div>
                  <div style="background:#f0fdf4;border:2px dashed #059669;padding:20px;text-align:center;border-radius:12px;margin:24px 0;">
                    <div style="font-size:12px;color:#047857;font-weight:700;letter-spacing:1px;text-transform:uppercase;margin-bottom:6px;">Your Coupon Code</div>
                    <div style="font-size:32px;font-weight:800;letter-spacing:6px;color:#059669;">${codeObtained}</div>
                    <div style="font-size:13px;color:#16a34a;margin-top:6px;">10% OFF • Single-Use Only</div>
                  </div>
                  <p style="color:#475569;font-size:14px;line-height:1.6;">
                    Present this code at physical store checkout or mention it upon delivery. This code is valid for <strong>30 days</strong>.
                  </p>
                  <hr style="border:none;border-top:1px solid #e2e8f0;margin:24px 0;" />
                  <p style="color:#94a3b8;font-size:12px;text-align:center;margin:0;">
                    StoreCraft E-Marketplace • If you did not request this coupon, please ignore this email.
                  </p>
                </div>`,
              }),
            });
            if (!res.ok) {
              console.warn('Brevo API returned non-OK:', res.status, await res.text());
            }
          } catch (mailErr) {
            console.warn('Brevo email attempt (non-fatal):', mailErr);
          }
        }
      }

      setClaimedCode(codeObtained);
      setStatus('success');
      setMessage('Your coupon has been generated and emailed to you!');
    } catch (err) {
      console.error('claim-coupon error:', err);
      setStatus('error');
      setMessage(err?.message || 'Something went wrong. Please try again.');
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
                  <div className="ccb-success-icon">🎉</div>
                  <h3>Coupon Claimed!</h3>
                  <p>{message}</p>
                  {claimedCode && (
                    <div style={{
                      margin: '18px 0',
                      padding: '14px 20px',
                      background: '#f0fdf4',
                      border: '2px dashed #059669',
                      borderRadius: '8px',
                      textAlign: 'center',
                    }}>
                      <div style={{ fontSize: '12px', color: '#047857', fontWeight: 600, textTransform: 'uppercase', marginBottom: '4px' }}>
                        Your Coupon Code
                      </div>
                      <span style={{ fontSize: '26px', fontWeight: 'bold', letterSpacing: '4px', color: '#059669' }}>
                        {claimedCode}
                      </span>
                    </div>
                  )}
                  <p className="ccb-success-hint">
                    A copy has been sent to <strong>{email}</strong>.
                    Show this code in-store or enter it during checkout.
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
