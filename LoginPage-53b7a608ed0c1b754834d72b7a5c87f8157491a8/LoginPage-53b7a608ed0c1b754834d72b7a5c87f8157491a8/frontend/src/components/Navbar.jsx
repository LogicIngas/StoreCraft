import React from 'react';

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
    return (
        <header className="app-header">
            <div className="header-content">
                <h1 className="logo">🛍️ Kasi Connect</h1>
                <nav className="nav">
                    <button
                        onClick={() => setCurrentPage('storefront')}
                        className={`nav-button ${currentPage === 'storefront' ? 'active' : ''}`}
                    >
                        🏠 Storefront
                    </button>
                    <button
                        onClick={() => setCurrentPage('cart')}
                        className={`nav-button ${currentPage === 'cart' ? 'active' : ''}`}
                    >
                        🛒 Cart <span className="badge">{cartCount}</span>
                    </button>
                    <button
                        onClick={() => setCurrentPage('wishlist')}
                        className={`nav-button ${currentPage === 'wishlist' ? 'active' : ''}`}
                    >
                        ❤️ Wishlist <span className="badge">{wishlistCount}</span>
                    </button>
                    <button
                        onClick={() => setCurrentPage('orders')}
                        className={`nav-button ${currentPage === 'orders' ? 'active' : ''}`}
                    >
                        📋 Orders
                    </button>
                    <button
                        onClick={() => setShowAddressManager(true)}
                        className="nav-button"
                    >
                        📍 Addresses
                    </button>
                    {isSeller && (
                        <button
                            onClick={() => setCurrentPage('upload')}
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