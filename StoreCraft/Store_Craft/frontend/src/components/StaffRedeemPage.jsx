/**
 * StaffRedeemPage.jsx
 * Staff-only page to redeem coupon codes.
 * Calls the `redeem-coupon` Supabase Edge Function.
 * Accessible only to users with roleName === 'ADMIN' or 'SELLER'.
 */

import React, { useState } from 'react';
import { supabase } from '../supabaseClient';
import './StaffRedeemPage.css';

export default function StaffRedeemPage({ currentUser }) {
  const [code, setCode] = useState('');
  const [status, setStatus] = useState('idle'); // idle | loading | success | error
  const [result, setResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');
  const [history, setHistory] = useState([]);

  const handleRedeem = async (e) => {
    e.preventDefault();

    const trimmedCode = code.trim().toUpperCase();
    if (!trimmedCode) {
      setStatus('error');
      setErrorMsg('Please enter a coupon code.');
      return;
    }

    setStatus('loading');
    setResult(null);
    setErrorMsg('');

    try {
      let redeemed = false;
      let redemptionData = null;

      // 1. Try Supabase Edge Function first
      try {
        const { data, error } = await supabase.functions.invoke('redeem-coupon', {
          body: {
            code: trimmedCode,
            staff_id: currentUser?.userId || 'unknown',
          },
        });

        if (!error && data?.ok) {
          redeemed = true;
          redemptionData = data.redemption;
        } else if (data?.error) {
          setStatus('error');
          setErrorMsg(data.error);
          setHistory((h) => [
            { code: trimmedCode, success: false, time: new Date() },
            ...h.slice(0, 19),
          ]);
          return;
        }
      } catch (edgeErr) {
        console.warn('Edge function unavailable, using direct fallback:', edgeErr);
      }

      // 2. Direct database fallback if Edge Function is not deployed
      if (!redeemed) {
        // Find coupon by code
        const { data: coupon, error: cErr } = await supabase
          .from('coupons')
          .select('coupon_id, code, is_active, usage_count, usage_limit')
          .eq('code', trimmedCode)
          .maybeSingle();

        if (cErr) throw cErr;
        if (!coupon) {
          setStatus('error');
          setErrorMsg('Coupon code not found.');
          setHistory((h) => [
            { code: trimmedCode, success: false, time: new Date() },
            ...h.slice(0, 19),
          ]);
          return;
        }

        // Update product_coupons row to redeemed
        const { data: updated, error: pcErr } = await supabase
          .from('product_coupons')
          .update({
            status: 'redeemed',
            redeemed_at: new Date().toISOString(),
            redeemed_by: currentUser?.userId || 'staff',
          })
          .eq('coupon_id', coupon.coupon_id)
          .eq('status', 'claimed')
          .select()
          .maybeSingle();

        if (pcErr) throw pcErr;
        if (!updated) {
          setStatus('error');
          setErrorMsg('Coupon is already redeemed, expired, or invalid.');
          setHistory((h) => [
            { code: trimmedCode, success: false, time: new Date() },
            ...h.slice(0, 19),
          ]);
          return;
        }

        // Increment usage count and mark coupon inactive
        await supabase
          .from('coupons')
          .update({ usage_count: (coupon.usage_count || 0) + 1, is_active: false })
          .eq('coupon_id', coupon.coupon_id);

        redemptionData = updated;
      }

      setStatus('success');
      setResult(redemptionData);
      setCode('');
      setHistory((h) => [
        { code: trimmedCode, success: true, time: new Date(), data: redemptionData },
        ...h.slice(0, 19),
      ]);
    } catch (err) {
      console.error('redeem-coupon error:', err);
      setStatus('error');
      setErrorMsg(err?.message || 'Network error. Please try again.');
    }
  };

  const handleReset = () => {
    setStatus('idle');
    setResult(null);
    setErrorMsg('');
    setCode('');
  };

  return (
    <div className="srp-page">
      <div className="srp-container">
        {/* Header */}
        <div className="srp-header">
          <div className="srp-header-icon">🏷️</div>
          <div>
            <h1 className="srp-title">Coupon Redemption</h1>
            <p className="srp-subtitle">
              Enter a customer's coupon code to mark it as redeemed
            </p>
          </div>
        </div>

        {/* Main form card */}
        <div className="srp-card">
          {status === 'success' && result ? (
            <div className="srp-success">
              <div className="srp-success-icon">✅</div>
              <h2 className="srp-success-title">Coupon Redeemed!</h2>
              <p className="srp-success-msg">
                The coupon has been successfully marked as redeemed.
              </p>
              <div className="srp-detail-grid">
                <div className="srp-detail-item">
                  <span className="srp-detail-label">Email</span>
                  <span className="srp-detail-value">{result.email || '—'}</span>
                </div>
                <div className="srp-detail-item">
                  <span className="srp-detail-label">Status</span>
                  <span className="srp-detail-value srp-status-badge srp-status-badge--redeemed">
                    {result.status}
                  </span>
                </div>
                <div className="srp-detail-item">
                  <span className="srp-detail-label">Redeemed at</span>
                  <span className="srp-detail-value">
                    {result.redeemed_at
                      ? new Date(result.redeemed_at).toLocaleString('en-ZA')
                      : '—'}
                  </span>
                </div>
                <div className="srp-detail-item">
                  <span className="srp-detail-label">Staff ID</span>
                  <span className="srp-detail-value">{result.redeemed_by || '—'}</span>
                </div>
              </div>
              <button
                type="button"
                className="srp-btn srp-btn--primary"
                onClick={handleReset}
                id="srp-redeem-another-btn"
              >
                Redeem Another
              </button>
            </div>
          ) : (
            <form onSubmit={handleRedeem} className="srp-form" noValidate>
              <div className="srp-field">
                <label htmlFor="srp-code-input" className="srp-label">
                  Coupon Code
                </label>
                <div className="srp-input-row">
                  <input
                    id="srp-code-input"
                    type="text"
                    className="srp-input"
                    placeholder="SAVE-XXXXXX"
                    value={code}
                    onChange={(e) => {
                      setCode(e.target.value.toUpperCase());
                      if (status === 'error') setStatus('idle');
                    }}
                    autoComplete="off"
                    spellCheck="false"
                    disabled={status === 'loading'}
                    maxLength={20}
                  />
                  <button
                    type="submit"
                    id="srp-redeem-btn"
                    className="srp-btn srp-btn--primary"
                    disabled={status === 'loading' || !code.trim()}
                  >
                    {status === 'loading' ? (
                      <><span className="srp-spinner" /> Checking…</>
                    ) : (
                      'Redeem'
                    )}
                  </button>
                </div>

                {status === 'error' && (
                  <div className="srp-error-msg" role="alert">
                    ❌ {errorMsg}
                  </div>
                )}
              </div>

              <p className="srp-hint">
                Codes follow the format <code>SAVE-XXXXXX</code> (uppercase).
                A code can only be redeemed once.
              </p>
            </form>
          )}
        </div>

        {/* Recent history */}
        {history.length > 0 && (
          <div className="srp-history">
            <h3 className="srp-history-title">Recent Redemptions (this session)</h3>
            <ul className="srp-history-list">
              {history.map((entry, i) => (
                <li key={i} className={`srp-history-item ${entry.success ? 'srp-history-item--ok' : 'srp-history-item--fail'}`}>
                  <span className="srp-history-code">{entry.code}</span>
                  <span className={`srp-status-badge ${entry.success ? 'srp-status-badge--redeemed' : 'srp-status-badge--failed'}`}>
                    {entry.success ? 'Redeemed' : 'Failed'}
                  </span>
                  <span className="srp-history-time">
                    {entry.time.toLocaleTimeString('en-ZA', { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}
