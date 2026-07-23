import React, { useEffect, useState } from 'react';

export default function LandingPage({ onLoginClick, products = [] }) {
    const [featuredProducts, setFeaturedProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadProducts = async () => {
            try {
                const response = await fetch('http://localhost:8080/product/all');
                const data = await response.json();
                // Get first 6 products for showcase
                setFeaturedProducts(data.slice(0, 6));
            } catch (error) {
                console.error('Error loading products:', error);
            } finally {
                setLoading(false);
            }
        };

        loadProducts();
    }, []);

    return (
        <div className="landing-page">
            {/* HERO SECTION */}
            <section className="hero-section">
                <div className="hero-content">
                    <div className="hero-text">
                        <h1 className="hero-title">
                            Welcome to <span className="gradient-text">AfriConnect</span>
                        </h1>
                        <p className="hero-subtitle">
                            Discover authentic African products, connect with sellers, and experience premium shopping
                        </p>
                        <div className="hero-buttons">
                            <button onClick={onLoginClick} className="btn-primary-large">
                                🔐 Get Started - Sign In Now
                            </button>
                            <button className="btn-secondary-large">
                                📚 Learn More
                            </button>
                        </div>
                    </div>
                    <div className="hero-image">
                        <div className="hero-placeholder">
                            <span>🛍️</span>
                        </div>
                    </div>
                </div>
            </section>

            {/* FEATURES SECTION */}
            <section className="features-section">
                <h2 className="section-title">Why Choose AfriConnect?</h2>
                <div className="features-grid">
                    <div className="feature-card">
                        <div className="feature-icon">🌍</div>
                        <h3>Global Reach</h3>
                        <p>Connect with authentic African products from across the continent</p>
                    </div>
                    <div className="feature-card">
                        <div className="feature-icon">🔒</div>
                        <h3>Secure Payments</h3>
                        <p>Safe and encrypted payment processing for your peace of mind</p>
                    </div>
                    <div className="feature-card">
                        <div className="feature-icon">⚡</div>
                        <h3>Fast Delivery</h3>
                        <p>Quick shipping with real-time order tracking</p>
                    </div>
                    <div className="feature-card">
                        <div className="feature-icon">💬</div>
                        <h3>24/7 Support</h3>
                        <p>Dedicated customer service team ready to help</p>
                    </div>
                    <div className="feature-card">
                        <div className="feature-icon">⭐</div>
                        <h3>Quality Assured</h3>
                        <p>Every product is verified for authenticity and quality</p>
                    </div>
                    <div className="feature-card">
                        <div className="feature-icon">🎁</div>
                        <h3>Exclusive Deals</h3>
                        <p>Special offers and discounts for our community</p>
                    </div>
                </div>
            </section>

            {/* FEATURED PRODUCTS SECTION */}
            <section className="featured-products-section">
                <h2 className="section-title">🌟 Featured Products</h2>
                <p className="section-subtitle">Browse our popular items - Sign in to shop</p>

                {loading ? (
                    <div className="loading-container">
                        <div className="spinner"></div>
                        <p>Loading products...</p>
                    </div>
                ) : featuredProducts.length === 0 ? (
                    <div className="empty-state">
                        <p>No products available yet</p>
                    </div>
                ) : (
                    <div className="featured-products-grid">
                        {featuredProducts.map(product => (
                            <div key={product.productId} className="featured-product-card">
                                <div className="product-image-wrapper">
                                    <img
                                        src={getImageUrl(product.imageUrl)}
                                        alt={product.name}
                                        className="product-image-landing"
                                        onError={(e) => {
                                            e.target.src = 'https://placehold.co/300x350?text=No+Image';
                                        }}
                                    />
                                    <div className="product-overlay">
                                        <button onClick={onLoginClick} className="btn-view-details">
                                            View Details →
                                        </button>
                                    </div>
                                </div>
                                <div className="product-info-landing">
                                    <span className="product-category-badge">{product.category}</span>
                                    <h3>{product.name}</h3>
                                    <p className="product-price-landing">R {product.price}</p>
                                    <div className="product-rating-landing">
                                        <span>⭐⭐⭐⭐⭐</span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                <div className="cta-section">
                    <p>Interested in these products?</p>
                    <button onClick={onLoginClick} className="btn-primary-large">
                        🔐 Sign In to Start Shopping
                    </button>
                </div>
            </section>

            {/* STATS SECTION */}
            <section className="stats-section">
                <h2 className="section-title">AfriConnect by Numbers</h2>
                <div className="stats-grid">
                    <div className="stat-card">
                        <h3>10K+</h3>
                        <p>Products</p>
                    </div>
                    <div className="stat-card">
                        <h3>50K+</h3>
                        <p>Happy Customers</p>
                    </div>
                    <div className="stat-card">
                        <h3>1000+</h3>
                        <p>Verified Sellers</p>
                    </div>
                    <div className="stat-card">
                        <h3>98%</h3>
                        <p>Satisfaction Rate</p>
                    </div>
                </div>
            </section>

            {/* CTA SECTION */}
            <section className="final-cta-section">
                <div className="cta-content">
                    <h2>Ready to Join the AfriConnect Community?</h2>
                    <p>Sign in to explore thousands of authentic African products</p>
                    <button onClick={onLoginClick} className="btn-primary-large-white">
                        🚀 Sign In Now
                    </button>
                </div>
            </section>
        </div>
    );
}

// Helper function
function getImageUrl(imageUrl) {
    if (!imageUrl) {
        return 'https://placehold.co/300x350?text=No+Image';
    }
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
    }
    return `http://localhost:8080${imageUrl}`;
}