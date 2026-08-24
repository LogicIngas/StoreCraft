import React from 'react';

export default function Footer() {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="footer">
            {/* FOOTER CONTENT */}
            <div className="footer-content">
                <div className="footer-section">
                    <div className="footer-brand">
                        <h3>🌍 StoreCraft</h3>
                        <p>Your gateway to authentic African products</p>
                        <div className="social-links">
                            <a href="#" className="social-link" title="Facebook">f</a>
                            <a href="#" className="social-link" title="Twitter">𝕏</a>
                            <a href="#" className="social-link" title="Instagram">📷</a>
                            <a href="#" className="social-link" title="LinkedIn">in</a>
                        </div>
                    </div>
                </div>

                <div className="footer-section">
                    <h4>Shop</h4>
                    <ul>
                        <li><a href="#products">All Products</a></li>
                        <li><a href="#clothing">Clothing</a></li>
                        <li><a href="#electronics">Electronics</a></li>
                        <li><a href="#accessories">Accessories</a></li>
                        <li><a href="#deals">Special Deals</a></li>
                    </ul>
                </div>

                <div className="footer-section">
                    <h4>Support</h4>
                    <ul>
                        <li><a href="#help">Help Center</a></li>
                        <li><a href="#contact">Contact Us</a></li>
                        <li><a href="#track">Track Order</a></li>
                        <li><a href="#returns">Returns & Exchanges</a></li>
                        <li><a href="#shipping">Shipping Info</a></li>
                    </ul>
                </div>

                <div className="footer-section">
                    <h4>Company</h4>
                    <ul>
                        <li><a href="#about">About Us</a></li>
                        <li><a href="#careers">Careers</a></li>
                        <li><a href="#blog">Blog</a></li>
                        <li><a href="#press">Press</a></li>
                        <li><a href="#partners">Become a Seller</a></li>
                    </ul>
                </div>

                <div className="footer-section">
                    <h4>Newsletter</h4>
                    <p>Subscribe to get special offers and updates</p>
                    <div className="newsletter-form">
                        <input
                            id="newsletter-email"
                            name="newsletter-email"
                            type="email"
                            placeholder="Enter your email"
                            className="newsletter-input"
                            autoComplete="email"
                        />
                        <button className="newsletter-button">Subscribe</button>
                    </div>
                </div>
            </div>

            {/* FOOTER BOTTOM */}
            <div className="footer-bottom">
                <div className="footer-bottom-content">
                    <p>&copy; {currentYear} StoreCraft. All rights reserved.</p>
                    <div className="footer-links">
                        <a href="#privacy">Privacy Policy</a>
                        <a href="#terms">Terms of Service</a>
                        <a href="#cookies">Cookie Policy</a>
                        <a href="#compliance">Compliance</a>
                    </div>
                </div>
                <div className="payment-methods">
                    <span>We accept:</span>
                    <span>💳 💰 📱</span>
                </div>
            </div>
        </footer>
    );
}