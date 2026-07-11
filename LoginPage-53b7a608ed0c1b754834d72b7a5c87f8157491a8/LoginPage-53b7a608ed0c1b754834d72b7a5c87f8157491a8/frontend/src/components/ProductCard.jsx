import React, { useState } from 'react';

export default function ProductCard({
                                        product,
                                        onAddToCart,
                                        onAddToWishlist,
                                        onRemoveFromWishlist,
                                        isInWishlist = false,
                                        showRemoveFromWishlist = false
                                    }) {
    const [isHovered, setIsHovered] = useState(false);
    const [isRemoving, setIsRemoving] = useState(false);

    // Handle wishlist click
    const handleWishlistClick = async (e) => {
        e.stopPropagation();

        if (showRemoveFromWishlist && onRemoveFromWishlist) {
            // Remove from wishlist (on wishlist page)
            setIsRemoving(true);
            try {
                await onRemoveFromWishlist(product.wishlistId || product.productId);
            } catch (error) {
                console.error('Error removing from wishlist:', error);
            } finally {
                setIsRemoving(false);
            }
        } else if (onAddToWishlist) {
            // Add to wishlist (on storefront)
            onAddToWishlist(product.productId);
        }
    };

    // Handle add to cart
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
                    src={product.imageUrl || 'https://placehold.co/300x350?text=No+Image+Found'}
                    alt={product.name || 'Product'}
                    className="product-image"
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = 'https://placehold.co/300x350?text=No+Image+Found';
                    }}
                />
                {isHovered && (
                    <div className="product-quick-actions">
                        <button
                            onClick={handleAddToCartClick}
                            className="quick-add-button"
                            disabled={product.stockQuantity <= 0}
                        >
                            🛒 Quick Add
                        </button>
                    </div>
                )}
                {product.stockQuantity <= 0 && (
                    <div className="out-of-stock-badge">Out of Stock</div>
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
                    </div>
                </div>
                {product.stockQuantity > 0 && product.stockQuantity <= 5 && (
                    <div className="low-stock-badge">🔥 Only {product.stockQuantity} left!</div>
                )}
            </div>
        </div>
    );
}