import React, { useState, useEffect } from 'react';
import ProductCard from './ProductCard';

export default function WishlistPage({
    wishlist,
    onAddToCart,
    onRemoveFromWishlist,
    setCurrentPage
}) {
    const [sortBy, setSortBy] = useState('newest');
    const [filterBy, setFilterBy] = useState('all');
    const [filteredWishlist, setFilteredWishlist] = useState([]);
    const [selectedItems, setSelectedItems] = useState([]);
    const [showShareModal, setShowShareModal] = useState(false);
    const [toastMessage, setToastMessage] = useState('');
    const [toastType, setToastType] = useState('');

    // Calculate stats
    const totalItems = wishlist.length;
    const totalValue = wishlist.reduce((sum, item) => sum + (item.price || 0), 0);
    const categories = [...new Set(wishlist.map(item => item.category || 'Uncategorized'))];
    const inStockItems = wishlist.filter(item => (item.stockQuantity || 0) > 0).length;

    // Filter and sort wishlist
    useEffect(() => {
        let filtered = [...wishlist];

        // Apply filter
        if (filterBy === 'in-stock') {
            filtered = filtered.filter(item => (item.stockQuantity || 0) > 0);
        } else if (filterBy === 'low-stock') {
            filtered = filtered.filter(item => (item.stockQuantity || 0) > 0 && (item.stockQuantity || 0) <= 5);
        } else if (filterBy === 'out-of-stock') {
            filtered = filtered.filter(item => (item.stockQuantity || 0) <= 0);
        }

        // Apply sort
        switch (sortBy) {
            case 'newest':
                filtered.sort((a, b) => new Date(b.addedAt || b.createdAt) - new Date(a.addedAt || a.createdAt));
                break;
            case 'oldest':
                filtered.sort((a, b) => new Date(a.addedAt || a.createdAt) - new Date(b.addedAt || b.createdAt));
                break;
            case 'price-low':
                filtered.sort((a, b) => (a.price || 0) - (b.price || 0));
                break;
            case 'price-high':
                filtered.sort((a, b) => (b.price || 0) - (a.price || 0));
                break;
            case 'name':
                filtered.sort((a, b) => (a.productName || a.name || '').localeCompare(b.productName || b.name || ''));
                break;
            default:
                break;
        }

        setFilteredWishlist(filtered);
    }, [wishlist, sortBy, filterBy]);

    // Show toast notification
    const showToast = (message, type = 'success') => {
        setToastMessage(message);
        setToastType(type);
        setTimeout(() => setToastMessage(''), 3000);
    };

    // Bulk add all to cart
    const handleAddAllToCart = async () => {
        if (filteredWishlist.length === 0) {
            showToast('No items to add to cart', 'error');
            return;
        }

        let successCount = 0;
        for (const item of filteredWishlist) {
            try {
                await onAddToCart(item.productId);
                successCount++;
            } catch (error) {
                console.error('Failed to add item:', error);
            }
        }

        showToast(`✅ Added ${successCount} items to cart!`, 'success');
    };

    // Clear all wishlist items
    const handleClearAll = async () => {
        if (!window.confirm('Are you sure you want to remove all items from your wishlist?')) {
            return;
        }

        let removedCount = 0;
        for (const item of filteredWishlist) {
            try {
                await onRemoveFromWishlist(item.wishlistId);
                removedCount++;
            } catch (error) {
                console.error('Failed to remove item:', error);
            }
        }

        showToast(`🗑️ Removed ${removedCount} items from wishlist`, 'success');
    };

    // Share wishlist
    const handleShare = () => {
        const shareData = {
            items: wishlist.map(item => ({
                name: item.productName || item.name,
                price: item.price,
                category: item.category
            })),
            totalItems: totalItems,
            totalValue: totalValue
        };

        const shareLink = `${window.location.origin}/wishlist/shared/${btoa(JSON.stringify(shareData))}`;

        if (navigator.share) {
            navigator.share({
                title: 'My AfriConnect Wishlist',
                text: `Check out my wishlist! ${totalItems} items worth R${totalValue.toFixed(2)}`,
                url: shareLink
            }).catch(() => {});
        } else {
            navigator.clipboard.writeText(shareLink).then(() => {
                showToast('📋 Wishlist link copied to clipboard!', 'success');
            }).catch(() => {
                showToast('📋 Share link: ' + shareLink, 'info');
            });
        }
    };

    // Empty state with CTA
    if (wishlist.length === 0) {
        return (
            <div className="wishlist-container">
                <div className="empty-wishlist-cta">
                    <div className="empty-wishlist-icon">❤️</div>
                    <h3>Your wishlist is empty</h3>
                    <p>Start saving your favorite items from the storefront!</p>
                    <button
                        onClick={() => setCurrentPage('storefront')}
                        className="explore-btn"
                    >
                        🛍️ Explore Products
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="wishlist-container">
            {/* Toast Notification */}
            {toastMessage && (
                <div className={`wishlist-toast ${toastType}`}>
                    {toastMessage}
                </div>
            )}

            {/* Header */}
            <div className="wishlist-header">
                <h2 className="section-title">❤️ My Wishlist</h2>
                <span className="wishlist-count">{wishlist.length} items</span>
            </div>

            {/* Stats Section */}
            <div className="wishlist-stats">
                <div className="stat-card-mini">
                    <span className="stat-icon">📦</span>
                    <div>
                        <h4>{totalItems}</h4>
                        <p>Items Saved</p>
                    </div>
                </div>
                <div className="stat-card-mini">
                    <span className="stat-icon">💰</span>
                    <div>
                        <h4>R {totalValue.toFixed(2)}</h4>
                        <p>Total Value</p>
                    </div>
                </div>
                <div className="stat-card-mini">
                    <span className="stat-icon">🏷️</span>
                    <div>
                        <h4>{categories.length}</h4>
                        <p>Categories</p>
                    </div>
                </div>
                <div className="stat-card-mini">
                    <span className="stat-icon">✅</span>
                    <div>
                        <h4>{inStockItems}</h4>
                        <p>In Stock</p>
                    </div>
                </div>
            </div>

            {/* Controls */}
            <div className="wishlist-controls">
                <div className="control-group">
                    <label>Sort by:</label>
                    <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                        <option value="newest">Newest Added</option>
                        <option value="oldest">Oldest Added</option>
                        <option value="price-low">Price: Low to High</option>
                        <option value="price-high">Price: High to Low</option>
                        <option value="name">Name</option>
                    </select>
                </div>

                <div className="control-group">
                    <label>Filter:</label>
                    <select value={filterBy} onChange={(e) => setFilterBy(e.target.value)}>
                        <option value="all">All Items</option>
                        <option value="in-stock">In Stock</option>
                        <option value="low-stock">Low Stock (≤5)</option>
                        <option value="out-of-stock">Out of Stock</option>
                    </select>
                </div>

                <div className="bulk-actions">
                    <button
                        onClick={handleAddAllToCart}
                        className="bulk-action-btn add-all"
                        disabled={filteredWishlist.length === 0}
                    >
                        🛒 Add All
                    </button>
                    <button
                        onClick={handleClearAll}
                        className="bulk-action-btn clear-all"
                        disabled={filteredWishlist.length === 0}
                    >
                        🗑️ Clear All
                    </button>
                    <button
                        onClick={handleShare}
                        className="bulk-action-btn share"
                    >
                        📤 Share
                    </button>
                </div>
            </div>

            {/* Results count */}
            <div className="wishlist-results">
                {filteredWishlist.length === 0 ? (
                    <p className="no-results">No items match your filter criteria</p>
                ) : (
                    <p>Showing {filteredWishlist.length} of {wishlist.length} items</p>
                )}
            </div>

            {/* Product Grid */}
            {filteredWishlist.length > 0 && (
                <div className="product-grid">
                    {filteredWishlist.map((item, index) => (
                        <ProductCard
                            key={item.wishlistId || item.productId}
                            product={item}
                            onAddToCart={onAddToCart}
                            onRemoveFromWishlist={() => onRemoveFromWishlist(item.wishlistId)}
                            showRemoveFromWishlist={true}
                            index={index}
                        />
                    ))}
                </div>
            )}

            {/* Recommendations Section */}
            {wishlist.length >= 3 && (
                <div className="recommendations-section">
                    <h3>💡 You might also like</h3>
                    <p className="rec-subtitle">Based on your wishlist preferences</p>
                    <div className="product-grid">
                        {/* These would come from your backend recommendation API */}
                        {wishlist.slice(0, 4).map((item, idx) => (
                            <div key={`rec-${idx}`} className="rec-card">
                                <div className="rec-card-image">
                                    <span>🛍️</span>
                                </div>
                                <div className="rec-card-info">
                                    <h4>Similar {item.category || 'Product'}</h4>
                                    <p>Recommended for you</p>
                                    <button className="rec-view-btn">View →</button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}