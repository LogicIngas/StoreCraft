import React, { useState, useEffect } from 'react';
import './App.css';
import ProductCard from './components/ProductCard';
import UploadProduct from './components/UploadProduct';
import PaymentCheckout from './PaymentCheckout';
import AddressManager from "./AddressManager.jsx";

export default function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [cartCount, setCartCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [statusMessage, setStatusMessage] = useState('');
  const [statusType, setStatusType] = useState('');
  const [currentPage, setCurrentPage] = useState('storefront');
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showAddressManager, setShowAddressManager] = useState(false);
  const [shippingAddress, setShippingAddress] = useState('');
  const [paymentProcessing, setPaymentProcessing] = useState(false);
  const [wishlist, setWishlist] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [categories, setCategories] = useState(['all', 'Clothing', 'Electronics', 'Accessories']);

  // Show status message
  const showStatus = (msg, type = 'error') => {
    setStatusMessage(msg);
    setStatusType(type);
    setTimeout(() => setStatusMessage(''), 4000);
  };

  // Load products on mount
  useEffect(() => {
    loadProducts();
  }, []);

  // Load cart when user changes
  useEffect(() => {
    if (currentUser) {
      loadCart();
      loadWishlist();
    }
  }, [currentUser]);

  // Filter products when search or category changes
  useEffect(() => {
    filterProducts();
  }, [products, searchTerm, selectedCategory]);

  /**
   * Load all products from backend
   */
  const loadProducts = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/product/all');
      if (!response.ok) throw new Error('Failed to load products');
      const data = await response.json();
      setProducts(data);
      setFilteredProducts(data);
    } catch (error) {
      console.error('Error loading products:', error);
      showStatus('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Load user's cart
   */
  const loadCart = async () => {
    try {
      const response = await fetch(`http://localhost:8080/cart/${currentUser.userId}`);
      if (response.ok) {
        const cartData = await response.json();
        setCart(cartData);
        setCartCount(cartData.items.length);
      }
    } catch (error) {
      console.error('Error loading cart:', error);
    }
  };

  /**
   * Load user's wishlist
   */
  const loadWishlist = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/wishlist/user/${currentUser.userId}`);
      if (response.ok) {
        const data = await response.json();
        setWishlist(data);
      } else {
        console.error('Failed to load wishlist:', response.status);
      }
    } catch (error) {
      console.error('Error loading wishlist:', error);
    }
  };

  /**
   * Filter products by search and category
   */
  const filterProducts = () => {
    let filtered = products;

    if (selectedCategory !== 'all') {
      filtered = filtered.filter(p => p.category === selectedCategory);
    }

    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase().trim();
      filtered = filtered.filter(p =>
          p.name.toLowerCase().includes(term) ||
          p.description.toLowerCase().includes(term) ||
          p.category.toLowerCase().includes(term)
      );
    }

    setFilteredProducts(filtered);
  };

  /**
   * Handle login
   */
  const handleLogin = async (email, password) => {
    try {
      const response = await fetch('http://localhost:8080/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await response.json();
      if (!response.ok) {
        showStatus(data || 'Login failed');
        return;
      }

      setCurrentUser({
        ...data,
        roleName: data.roleName || 'BUYER'
      });
      setCurrentPage('storefront');
      showStatus('Login successful!', 'success');
    } catch (error) {
      showStatus('Login error: ' + error.message);
    }
  };

  /**
   * Handle registration
   */
  const handleRegister = async (email, password, firstName, lastName, roleName) => {
    try {
      const response = await fetch('http://localhost:8080/user/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, firstName, lastName, roleName })
      });

      const data = await response.json();
      if (!response.ok) {
        showStatus(data || 'Registration failed');
        return;
      }

      setCurrentUser({
        ...data,
        roleName: data.roleName || roleName || 'BUYER'
      });
      setCurrentPage('storefront');
      showStatus('Registration successful!', 'success');
    } catch (error) {
      showStatus('Registration error: ' + error.message);
    }
  };

  /**
   * Handle add to wishlist
   */
  const handleAddToWishlist = async (productId) => {
    if (!currentUser) {
      showStatus('Please login to add to wishlist');
      return;
    }

    try {
      console.log('➕ Adding to wishlist - Product:', productId);

      const response = await fetch('http://localhost:8080/api/wishlist/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          productId: productId
        })
      });

      if (!response.ok) {
        const error = await response.json();
        console.error('❌ Failed to add to wishlist:', error);
        showStatus(error.message || 'Failed to add to wishlist');
        return;
      }

      const result = await response.text();
      console.log('✅ Added to wishlist:', result);

      showStatus('Added to wishlist! ❤️', 'success');
      await loadWishlist();
    } catch (error) {
      console.error('❌ Error adding to wishlist:', error);
      showStatus('Error adding to wishlist: ' + error.message);
    }
  };

  /**
   * Handle remove from wishlist
   */
  const handleRemoveFromWishlist = async (productId) => {
    if (!currentUser) {
      showStatus('Please login to manage wishlist');
      return;
    }

    try {
      console.log('🗑️ Removing product from wishlist:', productId);

      const response = await fetch(
          `http://localhost:8080/api/wishlist/remove?userId=${currentUser.userId}&productId=${productId}`,
          { method: 'DELETE' }
      );

      if (!response.ok) {
        const errorText = await response.text();
        console.error('❌ Failed to remove from wishlist:', errorText);
        showStatus('Failed to remove from wishlist');
        return;
      }

      const result = await response.text();
      console.log('✅ Removed from wishlist:', result);

      await loadWishlist();
      showStatus('Removed from wishlist ❤️', 'success');
    } catch (error) {
      console.error('❌ Error removing from wishlist:', error);
      showStatus('Error removing from wishlist: ' + error.message);
    }
  };

  /**
   * Handle remove from wishlist by ID
   */
  const handleRemoveFromWishlistById = async (wishlistId) => {
    if (!currentUser) {
      showStatus('Please login to manage wishlist');
      return;
    }

    try {
      console.log('🗑️ Removing wishlist item by ID:', wishlistId);

      const response = await fetch(
          `http://localhost:8080/api/wishlist/remove/${wishlistId}`,
          { method: 'DELETE' }
      );

      if (!response.ok) {
        const errorText = await response.text();
        console.error('❌ Failed to remove from wishlist:', errorText);
        showStatus('Failed to remove from wishlist');
        return;
      }

      const result = await response.text();
      console.log('✅ Removed from wishlist:', result);

      await loadWishlist();
      showStatus('Removed from wishlist ❤️', 'success');
    } catch (error) {
      console.error('❌ Error removing from wishlist:', error);
      showStatus('Error removing from wishlist: ' + error.message);
    }
  };

  /**
   * Handle add to cart
   */
  const handleAddToCart = async (productId) => {
    if (!currentUser) {
      showStatus('Please login to add items to cart');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/cart/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          productId: productId,
          quantity: 1
        })
      });

      if (!response.ok) {
        const error = await response.json();
        showStatus(error.message || 'Failed to add to cart');
        return;
      }

      const updatedCart = await response.json();
      setCart(updatedCart);
      setCartCount(updatedCart.items.length);
      showStatus('Added to cart! 🛒', 'success');
    } catch (error) {
      showStatus('Error adding to cart: ' + error.message);
    }
  };

  /**
   * Handle remove from cart
   */
  const handleRemoveFromCart = async (cartItemId) => {
    if (!currentUser) return;

    try {
      const response = await fetch(
          `http://localhost:8080/cart/remove/${cartItemId}?userId=${currentUser.userId}`,
          { method: 'DELETE' }
      );

      if (!response.ok) throw new Error('Failed to remove item');

      const updatedCart = await response.json();
      setCart(updatedCart);
      setCartCount(updatedCart.items.length);
      showStatus('Item removed', 'success');
    } catch (error) {
      showStatus('Error removing item: ' + error.message);
    }
  };

  /**
   * Handle update cart item quantity
   */
  const handleUpdateQuantity = async (cartItemId, newQuantity) => {
    if (!currentUser) return;

    if (newQuantity <= 0) {
      handleRemoveFromCart(cartItemId);
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/cart/update/${cartItemId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          quantity: newQuantity
        })
      });

      if (!response.ok) throw new Error('Failed to update quantity');

      const updatedCart = await response.json();
      setCart(updatedCart);
      setCartCount(updatedCart.items.length);
    } catch (error) {
      showStatus('Error updating quantity: ' + error.message);
    }
  };

  /**
   * Handle checkout (open payment modal)
   */
  const handleCheckout = () => {
    if (!currentUser) {
      showStatus('Please login to checkout');
      return;
    }
    if (!cart || cart.items.length === 0) {
      showStatus('Your cart is empty');
      return;
    }
    setShowPaymentModal(true);
  };

  /**
   * Handle payment submission
   */
  const handlePayment = async (cardToken, cardLast4, cardHolderName) => {
    if (!shippingAddress.trim()) {
      showStatus('Please enter a shipping address');
      return;
    }

    setPaymentProcessing(true);

    try {
      const response = await fetch('http://localhost:8080/payment/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          shippingAddress: shippingAddress,
          cardToken: cardToken,
          cardLast4: cardLast4,
          cardHolderName: cardHolderName
        })
      });

      const data = await response.json();

      if (!response.ok) {
        showStatus(data.message || 'Payment failed');
        setPaymentProcessing(false);
        return;
      }

      showStatus('Payment successful! Order confirmed. 🎉', 'success');
      setShowPaymentModal(false);
      setShippingAddress('');
      setCart(null);
      setCartCount(0);
      setCurrentPage('orders');
      setPaymentProcessing(false);

    } catch (error) {
      showStatus('Payment error: ' + error.message);
      setPaymentProcessing(false);
    }
  };

  /**
   * Handle product uploaded
   */
  const handleProductAdded = (newProduct) => {
    setProducts([...products, newProduct]);
    setCurrentPage('storefront');
    showStatus('Product uploaded successfully!', 'success');
  };

  /**
   * Handle logout
   */
  const handleLogout = () => {
    setCurrentUser(null);
    setCart(null);
    setCartCount(0);
    setCurrentPage('storefront');
    showStatus('Logged out', 'success');
  };

  // Render login/register page
  if (!currentUser) {
    return <LoginRegisterPage onLogin={handleLogin} onRegister={handleRegister} />;
  }

  // Check if user is a seller
  const isSeller = currentUser.roleName === 'SELLER';

  // Render main app
  return (
      <div className="app">
        {/* Header */}
        <header className="app-header">
          <div className="header-content">
            <h1 className="logo">🛍️ E-Marketplace</h1>
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
                ❤️ Wishlist <span className="badge">{wishlist.length}</span>
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

        {/* Status Message */}
        {statusMessage && (
            <div className={`status-banner ${statusType}`}>
              {statusMessage}
            </div>
        )}

        {/* Main Content */}
        <main className="main-content">
          {currentPage === 'storefront' && (
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
                              onAddToCart={handleAddToCart}
                              onAddToWishlist={handleAddToWishlist}
                              onRemoveFromWishlist={handleRemoveFromWishlist}
                              isInWishlist={wishlist.some(w => w.productId === product.productId)}
                          />
                      ))}
                    </div>
                )}
              </section>
          )}

          {currentPage === 'wishlist' && (
              <div className="wishlist-container">
                <h2 className="section-title">❤️ My Wishlist</h2>
                {wishlist.length === 0 ? (
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
                ) : (
                    <div className="product-grid">
                      {wishlist.map(item => (
                          <ProductCard
                              key={item.wishlistId || item.productId}
                              product={item}
                              onAddToCart={handleAddToCart}
                              onRemoveFromWishlist={() => handleRemoveFromWishlistById(item.wishlistId)}
                              showRemoveFromWishlist={true}
                          />
                      ))}
                    </div>
                )}
              </div>
          )}

          {currentPage === 'cart' && (
              <CartPage
                  cart={cart}
                  onRemove={handleRemoveFromCart}
                  onUpdateQuantity={handleUpdateQuantity}
                  onCheckout={handleCheckout}
              />
          )}

          {currentPage === 'orders' && (
              <OrdersPage userId={currentUser.userId} />
          )}

          {currentPage === 'upload' && isSeller && (
              <UploadProduct onProductAdded={handleProductAdded} />
          )}
        </main>

        {/* Address Manager Modal */}
        {showAddressManager && (
            <div className="modal-overlay">
              <AddressManager
                  userId={currentUser.userId}
                  onClose={() => setShowAddressManager(false)}
              />
            </div>
        )}

        {/* Payment Checkout Modal */}
        {showPaymentModal && (
            <PaymentCheckout
                cart={cart}
                shippingAddress={shippingAddress}
                onShippingAddressChange={setShippingAddress}
                onPayment={handlePayment}
                onClose={() => setShowPaymentModal(false)}
                isProcessing={paymentProcessing}
            />
        )}
      </div>
  );
}

/**
 * Login/Register Component with Role Selection
 */
function LoginRegisterPage({ onLogin, onRegister }) {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [selectedRole, setSelectedRole] = useState('BUYER');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isLogin) {
      onLogin(email, password);
    } else {
      onRegister(email, password, firstName, lastName, selectedRole);
    }
  };

  return (
      <div className="auth-container">
        <div className="auth-card">
          <div className="auth-logo">🛍️</div>
          <h1 className="auth-title">E-Marketplace</h1>
          <p className="auth-subtitle">{isLogin ? 'Welcome back!' : 'Create your account'}</p>

          <form onSubmit={handleSubmit} className="auth-form">
            <input
                type="email"
                placeholder="📧 Email Address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="auth-input"
                required
            />

            <div className="password-input-wrapper">
              <input
                  type={showPassword ? 'text' : 'password'}
                  placeholder="🔒 Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="auth-input"
                  required
              />
              <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>

            {!isLogin && (
                <>
                  <div className="auth-name-row">
                    <input
                        type="text"
                        placeholder="First Name"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        className="auth-input auth-name-input"
                    />
                    <input
                        type="text"
                        placeholder="Last Name"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        className="auth-input auth-name-input"
                    />
                  </div>

                  <div className="auth-role-selection">
                    <label className="auth-role-label">Select Account Type:</label>
                    <div className="auth-role-options">
                      <label className="auth-role-option">
                        <input
                            type="radio"
                            name="role"
                            value="BUYER"
                            checked={selectedRole === 'BUYER'}
                            onChange={(e) => setSelectedRole(e.target.value)}
                        />
                        <span className="role-option-label">
                      <span className="role-icon">🛒</span>
                      Buyer
                      <span className="role-description">Browse & purchase products</span>
                    </span>
                      </label>
                      <label className="auth-role-option">
                        <input
                            type="radio"
                            name="role"
                            value="SELLER"
                            checked={selectedRole === 'SELLER'}
                            onChange={(e) => setSelectedRole(e.target.value)}
                        />
                        <span className="role-option-label">
                      <span className="role-icon">📤</span>
                      Seller
                      <span className="role-description">Upload & sell products</span>
                    </span>
                      </label>
                    </div>
                  </div>
                </>
            )}

            <button type="submit" className="auth-button">
              {isLogin ? 'Sign In' : 'Create Account'}
            </button>

            <button
                type="button"
                onClick={() => setIsLogin(!isLogin)}
                className="auth-toggle"
            >
              {isLogin ? "Don't have an account? Register" : "Already have an account? Login"}
            </button>
          </form>
        </div>
      </div>
  );
}

/**
 * Cart Page Component
 */
function CartPage({ cart, onRemove, onUpdateQuantity, onCheckout }) {
  if (!cart || cart.items.length === 0) {
    return (
        <div className="empty-state">
          <div className="empty-state-icon">🛒</div>
          <h2>Your cart is empty</h2>
          <p>Start shopping to add items to your cart!</p>
        </div>
    );
  }

  return (
      <div className="cart-container">
        <h2 className="section-title">🛒 Shopping Cart</h2>
        <div className="cart-items">
          {cart.items.map(item => (
              <div key={item.cartItemId} className="cart-item">
                <div className="cart-item-info">
                  <h3>{item.productName}</h3>
                  <p className="cart-item-price">R {item.price}</p>
                </div>
                <div className="cart-item-controls">
                  <button
                      onClick={() => onUpdateQuantity(item.cartItemId, item.quantity - 1)}
                      className="quantity-button"
                  >
                    −
                  </button>
                  <span className="quantity-display">{item.quantity}</span>
                  <button
                      onClick={() => onUpdateQuantity(item.cartItemId, item.quantity + 1)}
                      className="quantity-button"
                  >
                    +
                  </button>
                  <span className="cart-item-subtotal">R {item.subtotal}</span>
                  <button
                      onClick={() => onRemove(item.cartItemId)}
                      className="remove-button"
                  >
                    Remove
                  </button>
                </div>
              </div>
          ))}
        </div>
        <div className="cart-summary">
          <div className="cart-total">
            <span>Total:</span>
            <strong>R {cart.total}</strong>
          </div>
          <button onClick={onCheckout} className="checkout-button">
            Proceed to Checkout →
          </button>
        </div>
      </div>
  );
}

/**
 * Orders Page Component
 */
function OrdersPage({ userId }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadOrders = async () => {
      try {
        const response = await fetch(`http://localhost:8080/order/user/${userId}`);
        if (response.ok) {
          const data = await response.json();
          setOrders(data);
        }
      } catch (error) {
        console.error('Error loading orders:', error);
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, [userId]);

  if (loading) return <div className="loading">Loading orders...</div>;

  if (orders.length === 0) {
    return (
        <div className="empty-state">
          <div className="empty-state-icon">📋</div>
          <h2>No orders yet</h2>
          <p>Start shopping to place your first order!</p>
        </div>
    );
  }

  return (
      <div className="orders-container">
        <h2 className="section-title">📋 My Orders</h2>
        {orders.map(order => (
            <div key={order.orderId} className="order-card">
              <div className="order-header">
                <div>
                  <h3>Order #{order.orderId.substring(0, 8)}</h3>
                  <p className="order-date">
                    {new Date(order.createdAt).toLocaleDateString('en-ZA', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </p>
                </div>
                <span className={`order-status ${order.status.toLowerCase()}`}>
              {order.status}
            </span>
              </div>
              <div className="order-items">
                {order.items.map(item => (
                    <div key={item.orderItemId} className="order-item">
                      <span>{item.productName}</span>
                      <span>x{item.quantity}</span>
                      <span>R {item.subtotal}</span>
                    </div>
                ))}
              </div>
              <div className="order-total">
                <strong>Total: R {order.totalAmount}</strong>
              </div>
            </div>
        ))}
      </div>
  );
}