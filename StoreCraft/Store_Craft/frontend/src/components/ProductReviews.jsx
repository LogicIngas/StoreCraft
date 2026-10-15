/**
 * ProductReviews.jsx
 * Combines ReviewForm (5-star picker + optional name/comment) and
 * ReviewsList (average rating + individual review cards).
 * Uses the `submit-review` Supabase Edge Function for writes.
 * Uses supabase.from('product_reviews') directly for reads (public SELECT policy).
 */

import React, { useState, useEffect, useCallback } from 'react';
import { supabase } from '../supabaseClient';
import './ProductReviews.css';

// ─── Helpers ──────────────────────────────────────────────────────────────────

function StarPicker({ value, onChange, size = 28 }) {
  const [hovered, setHovered] = useState(0);

  return (
    <div className="pr-star-picker" role="group" aria-label="Rating">
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          className={`pr-star-btn ${star <= (hovered || value) ? 'active' : ''}`}
          style={{ fontSize: size }}
          onMouseEnter={() => setHovered(star)}
          onMouseLeave={() => setHovered(0)}
          onClick={() => onChange(star)}
          aria-label={`${star} star${star > 1 ? 's' : ''}`}
        >
          ★
        </button>
      ))}
    </div>
  );
}

function StarDisplay({ value, size = 16 }) {
  return (
    <span className="pr-star-display" aria-label={`${value} out of 5 stars`}>
      {[1, 2, 3, 4, 5].map((s) => (
        <span
          key={s}
          style={{ fontSize: size, color: s <= Math.round(value) ? '#f59e0b' : '#e2e8f0' }}
        >
          ★
        </span>
      ))}
    </span>
  );
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('en-ZA', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  });
}

// ─── ReviewForm ───────────────────────────────────────────────────────────────

function ReviewForm({ productId, currentUser, onReviewSubmitted }) {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [reviewerName, setReviewerName] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [toast, setToast] = useState(null);

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 4000);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (rating === 0) {
      showToast('Please select a star rating.', 'error');
      return;
    }

    setSubmitting(true);
    try {
      let inserted = false;
      try {
        const { data, error } = await supabase.functions.invoke('submit-review', {
          body: {
            product_id: productId,
            rating,
            comment: comment.trim() || undefined,
            reviewer_name: reviewerName.trim() || undefined,
            user_id: currentUser?.userId || null,
          },
        });
        if (!error && data?.ok) {
          inserted = true;
        }
      } catch (edgeErr) {
        console.warn('Edge function unavailable, using direct database fallback:', edgeErr);
      }

      if (!inserted) {
        const cleanComment = comment.trim() || null;
        const displayName = reviewerName.trim() || (currentUser?.userId ? null : 'Anonymous');
        const { error: insertErr } = await supabase
          .from('product_reviews')
          .insert({
            product_id: productId,
            rating,
            comment: cleanComment,
            review_text: cleanComment,
            reviewer_name: displayName,
            is_anonymous: !currentUser?.userId,
            user_id: currentUser?.userId || null,
          });

        if (insertErr) {
          console.error('Direct insert error:', insertErr);
          showToast(insertErr.message || 'Failed to submit review.', 'error');
          return;
        }
      }

      showToast('Your review has been submitted! Thank you 🎉', 'success');
      setRating(0);
      setComment('');
      setReviewerName('');
      onReviewSubmitted?.();
    } catch (err) {
      console.error('submit-review error:', err);
      showToast('Something went wrong. Please try again.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="pr-form-wrapper">
      <h3 className="pr-form-title">Leave a Review</h3>
      <p className="pr-form-subtitle">
        Share your experience — no account required!
      </p>

      {toast && (
        <div className={`pr-toast pr-toast--${toast.type}`} role="alert">
          {toast.type === 'success' ? '✅' : '❌'} {toast.msg}
        </div>
      )}

      <form onSubmit={handleSubmit} className="pr-form" noValidate>
        {/* Star picker */}
        <div className="pr-field">
          <label className="pr-label">Your Rating *</label>
          <StarPicker value={rating} onChange={setRating} size={32} />
          {rating > 0 && (
            <span className="pr-rating-label">
              {['', 'Poor', 'Fair', 'Good', 'Great', 'Excellent!'][rating]}
            </span>
          )}
        </div>

        {/* Display name */}
        <div className="pr-field">
          <label htmlFor="pr-name" className="pr-label">
            Your Name <span className="pr-optional">(optional)</span>
          </label>
          <input
            id="pr-name"
            type="text"
            className="pr-input"
            placeholder="e.g. John D. or leave blank for Anonymous"
            value={reviewerName}
            onChange={(e) => setReviewerName(e.target.value)}
            maxLength={80}
          />
        </div>

        {/* Comment */}
        <div className="pr-field">
          <label htmlFor="pr-comment" className="pr-label">
            Comment <span className="pr-optional">(optional)</span>
          </label>
          <textarea
            id="pr-comment"
            className="pr-textarea"
            placeholder="Tell us what you think..."
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            rows={3}
            maxLength={1000}
          />
          <span className="pr-char-count">{comment.length}/1000</span>
        </div>

        <button
          type="submit"
          className="pr-submit-btn"
          disabled={submitting || rating === 0}
        >
          {submitting ? (
            <span className="pr-spinner">⏳ Submitting…</span>
          ) : (
            '✍️ Submit Review'
          )}
        </button>
      </form>
    </div>
  );
}

// ─── ReviewsList ──────────────────────────────────────────────────────────────

function ReviewsList({ productId, refreshKey }) {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchReviews = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const { data, error: fetchErr } = await supabase
        .from('product_reviews')
        .select('*')
        .eq('product_id', productId)
        .order('created_at', { ascending: false });

      if (fetchErr) throw fetchErr;
      setReviews(data || []);
    } catch (err) {
      console.error('ReviewsList fetch error:', err);
      setError('Could not load reviews. Please refresh the page.');
    } finally {
      setLoading(false);
    }
  }, [productId]);

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews, refreshKey]);

  const avg =
    reviews.length > 0
      ? reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length
      : 0;

  if (loading) {
    return (
      <div className="pr-list-loading">
        <div className="pr-skeleton" />
        <div className="pr-skeleton pr-skeleton--short" />
        <div className="pr-skeleton" />
      </div>
    );
  }

  if (error) {
    return <p className="pr-list-error">{error}</p>;
  }

  return (
    <div className="pr-list-wrapper">
      {/* Summary bar */}
      <div className="pr-summary">
        <div className="pr-avg-score">
          {avg > 0 ? avg.toFixed(1) : '—'}
        </div>
        <div className="pr-avg-detail">
          <StarDisplay value={avg} size={20} />
          <span className="pr-review-count">
            {reviews.length} review{reviews.length !== 1 ? 's' : ''}
          </span>
        </div>
      </div>

      {reviews.length === 0 ? (
        <div className="pr-empty">
          <span className="pr-empty-icon">💬</span>
          <p>No reviews yet. Be the first to share your thoughts!</p>
        </div>
      ) : (
        <ul className="pr-cards" aria-label="Customer reviews">
          {reviews.map((review) => (
            <li key={review.review_id || review.id} className="pr-card">
              <div className="pr-card-header">
                <div className="pr-card-meta">
                  <span className="pr-card-author">
                    {review.is_anonymous
                      ? 'Anonymous'
                      : review.reviewer_name || 'Anonymous'}
                  </span>
                  <StarDisplay value={review.rating} size={14} />
                </div>
                <span className="pr-card-date">
                  {formatDate(review.created_at)}
                </span>
              </div>
              {(review.comment || review.review_text) && (
                <p className="pr-card-comment">{review.comment || review.review_text}</p>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

// ─── Exported composite ───────────────────────────────────────────────────────

export default function ProductReviews({ productId, currentUser }) {
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <section className="pr-section" aria-labelledby="pr-heading">
      <h2 id="pr-heading" className="pr-section-title">
        ⭐ Customer Reviews
      </h2>
      <div className="pr-layout">
        <ReviewsList productId={productId} refreshKey={refreshKey} />
        <ReviewForm
          productId={productId}
          currentUser={currentUser}
          onReviewSubmitted={() => setRefreshKey((k) => k + 1)}
        />
      </div>
    </section>
  );
}
