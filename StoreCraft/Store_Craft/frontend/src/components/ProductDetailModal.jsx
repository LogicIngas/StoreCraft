/**
 * ProductDetailModal.jsx
 * Full-screen product detail overlay with:
 *   - Product image, name, price, description, stock
 *   - ClaimCouponButton (Task 2)
 *   - Add to Cart / Wishlist buttons
 *   - ProductReviews section (Task 1)
 *
 * Opens when a ProductCard is clicked in the Storefront.
 */

import React, { useEffect } from 'react';
import { apiUrl } from '../api.js';
import ProductReviews from './ProductReviews';
import ClaimCouponButton from './ClaimCouponButton';
import './ProductDetailModal.css';

function getImageUrl(imageUrl) {
  if (!imageUrl) return 'https://placehold.co/600x500?text=No+Image';
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) return imageUrl;
  return apiUrl(imageUrl);
}

export default function ProductDetailModal({
  product,
  onClose,
  onAddToCart,
  onAddToWishlist,
  onRemoveFromWishlist,
  isInWishlist = false,
  isAdmin = false,
  currentUser = null,
}) {
  // Close on Escape
  useEffect(() => {
    const handle = (e) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', handle);
    return () => window.removeEventListener('keydown', handle);
  }, [onClose]);

  // Prevent body scroll while open
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = ''; };
  }, []);

  const inStock =
    product.stockQuantity == null || Number(product.stockQuantity) > 0;

  return (
    <div
      className="pdm-overlay"
      role="dialog"
      aria-modal="true"
      aria-label={product.name}
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div className="pdm-card">
        {/* Close */}
        <button
          type="button"
          className="pdm-close"
          onClick={onClose}
          aria-label="Close product detail"
        >
          ✕
        </button>

        {/* Top section */}
        <div className="pdm-top">
          {/* Image */}
          <div className="pdm-image-wrap">
            <img
              src={getImageUrl(product.imageUrl)}
              alt={product.name}
              className="pdm-image"
              onError={(e) => {
                e.target.onerror = null;
                e.target.src = 'https://placehold.co/600x500?text=No+Image';
              }}
            />
            {!inStock && (
              <div className="pdm-out-of-stock">Out of Stock</div>
            )}
          </div>

          {/* Info */}
          <div className="pdm-info">
            <span className="pdm-category">
              {product.category || 'Uncategorized'}
            </span>
            <h1 className="pdm-title">{product.name}</h1>

            <div className="pdm-price-row">
              <span className="pdm-price">R {product.price || 0}</span>
              {inStock &&
                product.stockQuantity != null &&
                Number(product.stockQuantity) > 0 &&
                Number(product.stockQuantity) <= 5 && (
                  <span className="pdm-low-stock">
                    🔥 Only {product.stockQuantity} left!
                  </span>
                )}
            </div>

            <p className="pdm-description">
              {product.description || 'No description available.'}
            </p>

            {/* Stock count */}
            {product.stockQuantity != null && (
              <p className="pdm-stock-count">
                {inStock
                  ? `✅ In stock (${product.stockQuantity} units)`
                  : '❌ Out of stock'}
              </p>
            )}

            {/* Action buttons */}
            {!isAdmin && (
              <div className="pdm-actions">
                <button
                  type="button"
                  className="pdm-cart-btn"
                  disabled={!inStock}
                  onClick={() => onAddToCart?.(product.productId)}
                >
                  🛒 Add to Cart
                </button>

                <button
                  type="button"
                  className={`pdm-wishlist-btn ${isInWishlist ? 'pdm-wishlist-btn--active' : ''}`}
                  onClick={() =>
                    isInWishlist
                      ? onRemoveFromWishlist?.(product.productId)
                      : onAddToWishlist?.(product.productId)
                  }
                >
                  {isInWishlist ? '❤️ Wishlisted' : '🤍 Wishlist'}
                </button>

                <ClaimCouponButton
                  product={product}
                  currentUser={currentUser}
                />
              </div>
            )}
          </div>
        </div>

        {/* Reviews section */}
        <div className="pdm-reviews">
          <ProductReviews
            productId={product.productId}
            currentUser={currentUser}
          />
        </div>
      </div>
    </div>
  );
}
