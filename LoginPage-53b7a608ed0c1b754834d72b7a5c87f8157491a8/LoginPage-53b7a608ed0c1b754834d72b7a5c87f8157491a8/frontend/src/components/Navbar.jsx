import React, { useState } from 'react';

export default function Navbar({
                                   currentUser,
                                   currentPage,
                                   setCurrentPage,
                                   cartCount,
                                   wishlistCount,
                                   setShowAddressManager,
                                   handleLogout,
                                   isSeller
                               }) {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const toggleMobileMenu = () => {
        setIsMobileMenuOpen(!isMobileMenuOpen);
    };

    const handleNavClick = (page) => {
        setCurrentPage(page);
        setIsMobileMenuOpen(false);
    };

    return (
        <header className="app-header">
            <div className="header-content">
                <div className="header-left">
                    <h1 className="logo">🌍 AfriConnect</h1>

                    <button
                        className="hamburger-button"
                        onClick={toggleMobileMenu}
                        aria-label="Toggle navigation menu"
                    >
                        <span className="hamburger-icon">
                            {isMobileMenuOpen ? '✕' : '☰'}
                        </span>
                    </button>
                </div>

                <nav className={`nav ${isMobileMenuOpen ? 'mobile-open' : ''}`}>
                    <button
                        onClick={() => handleNavClick('storefront')}
                        className={`nav-button ${currentPage === 'storefront' ? 'active' : ''}`}
                    >
                        🏠 Storefront
                    </button>
                    <button
                        onClick={() => handleNavClick('cart')}
                        className={`nav-button ${currentPage === 'cart' ? 'active' : ''}`}
                    >
                        🛒 Cart <span className="badge">{cartCount}</span>
                    </button>
                    <button
                        onClick={() => handleNavClick('wishlist')}
                        className={`nav-button ${currentPage === 'wishlist' ? 'active' : ''}`}
                    >
                        ❤️ Wishlist <span className="badge">{wishlistCount}</span>
                    </button>
                    <button
                        onClick={() => handleNavClick('orders')}
                        className={`nav-button ${currentPage === 'orders' ? 'active' : ''}`}
                    >
                        📋 Orders
                    </button>
                    <button
                        onClick={() => {
                            setShowAddressManager(true);
                            setIsMobileMenuOpen(false);
                        }}
                        className="nav-button"
                    >
                        📍 Addresses
                    </button>
                    {isSeller && (
                        <button
                            onClick={() => handleNavClick('upload')}
                            className={`nav-button ${currentPage === 'upload' ? 'active' : ''}`}
                        >
                            📤 Upload
                        </button>
                    )}
                    <span className="user-role-badge">{currentUser.roleName}</span>
                    <button
                        onClick={handleLogout}
                        className="logout-button"
                    >
                        🚪 Logout
                    </button>
                </nav>
            </div>
        </header>
    );
}