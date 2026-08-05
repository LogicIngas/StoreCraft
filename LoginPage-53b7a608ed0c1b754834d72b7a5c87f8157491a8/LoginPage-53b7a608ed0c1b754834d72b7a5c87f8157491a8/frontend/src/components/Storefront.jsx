import React from 'react';
import ProductCard from './ProductCard';

export default function Storefront({
                                       products,
                                       filteredProducts,
                                       loading,
                                       searchTerm,
                                       setSearchTerm,
                                       selectedCategory,
                                       setSelectedCategory,
                                       categories,
                                       onAddToCart,
                                       onAddToWishlist,
                                       onRemoveFromWishlist,
                                       wishlist
                                   }) {
    return (
        <section className="section">
            <div className="section-header">
                <h2 className="section-title">✨ Featured Products</h2>
                <div className="section-actions">
                    <select
                        value={selectedCategory}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                        className="category-filter"
                    >
                        {categories.map(cat => (
                            <option key={cat} value={cat}>
                                {cat === 'all' ? 'All Categories' : cat}
                            </option>
                        ))}
                    </select>
                    <input
                        type="text"
                        placeholder="🔍 Search products..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>
            </div>

            {loading ? (
                <div className="loading">Loading products...</div>
            ) : filteredProducts.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-state-icon">🔍</div>
                    <p>No products found matching your criteria.</p>
                </div>
            ) : (
                <div className="product-grid">
                    {filteredProducts.map(product => (
                        <ProductCard
                            key={product.productId}
                            product={product}
                            onAddToCart={onAddToCart}
                            onAddToWishlist={onAddToWishlist}
                            onRemoveFromWishlist={onRemoveFromWishlist}
                            isInWishlist={wishlist.some(w => w.productId === product.productId)}
                        />
                    ))}
                </div>
            )}
        </section>
    );
}