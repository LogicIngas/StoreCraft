import React from 'react';
import ProductCard from './ProductCard';

export default function WishlistPage({
    wishlist,
    onAddToCart,
    onRemoveFromWishlist,
    setCurrentPage
}) {
    if (!wishlist || wishlist.length === 0) {
        return (
            <div className="empty-state">
                <div className="empty-state-icon">❤️</div>
                <h2>Your wishlist is empty</h2>
                <p>Start saving your favorite items!</p>
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
                        key={item.wishlistId}
                        // ✅ Pass the product object from the wishlist item
                        product={item.product || { 
                            productId: item.productId,
                            name: 'Product',
                            price: 0,
                            imageUrl: null,
                            description: '',
                            category: '',
                            stockQuantity: 0
                        }}
                        onAddToCart={onAddToCart}
                        onRemoveFromWishlist={() => onRemoveFromWishlist(item.wishlistId)}
                        showRemoveFromWishlist={true}
                        isInWishlist={true}
                    />
                ))}
            </div>
        </div>
    );
}