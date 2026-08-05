import React from 'react';
import ProductCard from './ProductCard';

export default function WishlistPage({
                                         wishlist,
                                         onAddToCart,
                                         onRemoveFromWishlist,
                                         setCurrentPage
                                     }) {
    if (wishlist.length === 0) {
        return (
            <div className="empty-state">
                <div className="empty-state-icon">❤️</div>
                <p>Your wishlist is empty. Start saving your favorite items!</p>
                <button
                    onClick={() => setCurrentPage('storefront')}
                    className="empty-state-button"
                >
                    Browse Products
                </button>
            </div>
        );
    }

    return (
        <div className="wishlist-container">
            <h2 className="section-title">❤️ My Wishlist</h2>
            <div className="product-grid">
                {wishlist.map(item => (
                    <ProductCard
                        key={item.wishlistId || item.productId}
                        product={item}
                        onAddToCart={onAddToCart}
                        onRemoveFromWishlist={() => onRemoveFromWishlist(item.wishlistId)}
                        showRemoveFromWishlist={true}
                    />
                ))}
            </div>
        </div>
    );
}