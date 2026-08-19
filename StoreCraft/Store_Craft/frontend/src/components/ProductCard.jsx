import React, { useState } from 'react';

function getImageUrl(imageUrl) {
  if (!imageUrl) return 'https://placehold.co/300x350?text=No+Image';
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) return imageUrl;
  return `http://localhost:8080${imageUrl}`;
}

export default function ProductCard({
  product,
  onAddToCart,
  onAddToWishlist,
  onRemoveFromWishlist,
  isInWishlist = false,
  showRemoveFromWishlist = false,
  isAdmin = false,
}) {
  const [isHovered, setIsHovered] = useState(false);
  const [isRemoving, setIsRemoving] = useState(false);

  const handleWishlistClick = async (e) => {
    e.stopPropagation();
    if (showRemoveFromWishlist && onRemoveFromWishlist) {
      setIsRemoving(true);
      try {
        await onRemoveFromWishlist(product.wishlistId || product.productId);
      } catch (error) {
        console.error('Error removing from wishlist:', error);
      } finally {
        setIsRemoving(false);
      }
    } else if (onAddToWishlist) {
      onAddToWishlist(product.productId);
    }
  };

  const handleAddToCartClick = (e) => {
    e.stopPropagation();
    if (onAddToCart) {
      onAddToCart(product.productId);
    }
  };

  return (
    <div
      className="product-card"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div className="product-image-container">
        <img
          src={getImageUrl(product.imageUrl)}
          alt={product.name || 'Product'}
          className="product-image"
          onError={(e) => {
            e.target.onerror = null;
            e.target.src = 'https://placehold.co/300x350?text=No+Image';
          }}
        />
        {isHovered && !isAdmin && (
          <div className="product-quick-actions">
            <button
              onClick={handleAddToCartClick}
              className="quick-add-button"
              disabled={product.stockQuantity <= 0 || isAdmin}
            >
              🛒 Quick Add
            </button>
          </div>
        )}
        {product.stockQuantity <= 0 && (
          <div className="out-of-stock-badge">Out of Stock</div>
        )}
        {isAdmin && (
          <div className="out-of-stock-badge" style={{ background: '#3b82f6' }}>
            Admin View
          </div>
        )}
      </div>
      <div className="product-info">
        <span className="product-category">{product.category || 'Uncategorized'}</span>
        <h3 className="product-title">{product.name || 'Unnamed Product'}</h3>
        <p className="product-description">
          {product.description || 'No description available'}
        </p>
        <div className="product-rating">
          <span>⭐⭐⭐⭐⭐</span>
          <span className="review-count">(24 reviews)</span>
        </div>
        <div className="product-footer">
          <span className="product-price">R {product.price || 0}</span>
          <div className="product-actions">
            {!isAdmin && (
              <>
                {showRemoveFromWishlist ? (
                  <button
                    onClick={handleWishlistClick}
                    className="wishlist-remove-button"
                    disabled={isRemoving}
                  >
                    {isRemoving ? 'Removing...' : '❌ Remove'}
                  </button>
                ) : (
                  <button
                    onClick={handleWishlistClick}
                    className={`wishlist-button ${isInWishlist ? 'active' : ''}`}
                    title={isInWishlist ? 'Remove from wishlist' : 'Add to wishlist'}
                  >
                    {isInWishlist ? '❤️' : '🤍'}
                  </button>
                )}
                <button
                  onClick={handleAddToCartClick}
                  className="add-to-cart-button"
                  disabled={product.stockQuantity <= 0}
                >
                  Add To Cart
                </button>
              </>
            )}
          </div>
        </div>
        {product.stockQuantity > 0 && product.stockQuantity <= 5 && !isAdmin && (
          <div className="low-stock-badge">🔥 Only {product.stockQuantity} left!</div>
        )}
      </div>
    </div>
  );
}