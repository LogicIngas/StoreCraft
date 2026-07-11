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
    }
  }, [currentUser]);

  /**
   * Load all products from backend
   */
  const loadProducts = async () => {
    try {
      const response = await fetch('http://localhost:8080/product/all');
      if (!response.ok) throw new Error('Failed to load products');
      const data = await response.json();
      setProducts(data);
    } catch (error) {
      console.error('Error loading products:', error);
      showStatus('Failed to load products');
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

      setCurrentUser(data);
      setCurrentPage('storefront');
      showStatus('Login successful!', 'success');
    } catch (error) {
      showStatus('Login error: ' + error.message);
    }
  };

  /**
   * Handle registration
   */
  const handleRegister = async (email, password, firstName, lastName) => {
    try {
      const response = await fetch('http://localhost:8080/user/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, firstName, lastName })
      });

      const data = await response.json();
      if (!response.ok) {
        showStatus(data || 'Registration failed');
        return;
      }

      setCurrentUser(data);
      setCurrentPage('storefront');
      showStatus('Registration successful!', 'success');
    } catch (error) {
      showStatus('Registration error: ' + error.message);
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
      showStatus('Added to cart!', 'success');
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

      // Payment successful
      showStatus('Payment successful! Order confirmed.', 'success');
      setShowPaymentModal(false);
      setShippingAddress('');
      setCart(null);
      setCartCount(0);
      setCurrentPage('storefront');
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

  // Render main app
  return (
      <div style={styles.app}>
        {/* Header */}
        <header style={styles.header}>
          <div style={styles.headerContent}>
            <h1 style={styles.logo}>🛍️ E-Marketplace</h1>
            <nav style={styles.nav}>
              <button
                  onClick={() => setCurrentPage('storefront')}
                  style={{
                    ...styles.navButton,
                    background: currentPage === 'storefront' ? '#0f766e' : 'transparent'
                  }}
              >
                Storefront
              </button>
              <button
                  onClick={() => setCurrentPage('cart')}
                  style={{
                    ...styles.navButton,
                    background: currentPage === 'cart' ? '#0f766e' : 'transparent'
                  }}
              >
                🛒 Cart ({cartCount})
              </button>
              <button
                  onClick={() => setCurrentPage('orders')}
                  style={{
                    ...styles.navButton,
                    background: currentPage === 'orders' ? '#0f766e' : 'transparent'
                  }}
              >
                My Orders
              </button>
              <button
                  onClick={() => setShowAddressManager(true)}
                  style={{
                    ...styles.navButton,
                    background: showAddressManager ? '#0f766e' : 'transparent'
                  }}
              >
                📍 Addresses
              </button>
              <button
                  onClick={() => setCurrentPage('upload')}
                  style={{
                    ...styles.navButton,
                    background: currentPage === 'upload' ? '#0f766e' : 'transparent'
                  }}
              >
                Upload Product
              </button>
              <button
                  onClick={handleLogout}
                  style={styles.logoutButton}
              >
                Logout
              </button>
            </nav>
          </div>
        </header>

        {/* Status Message */}
        {statusMessage && (
            <div style={{
              ...styles.statusBanner,
              background: statusType === 'success' ? '#10b981' : '#ef4444',
            }}>
              {statusMessage}
            </div>
        )}

        {/* Main Content */}
        <main style={styles.main}>
          {currentPage === 'storefront' && (
              <section style={styles.section}>
                <h2 style={styles.sectionTitle}>Featured Products</h2>
                <div style={styles.productGrid}>
                  {products.map(product => (
                      <ProductCard
                          key={product.productId}
                          product={product}
                          onAddToCart={handleAddToCart}
                      />
                  ))}
                </div>
              </section>
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

          {currentPage === 'upload' && (
              <UploadProduct onProductAdded={handleProductAdded} />
          )}
        </main>

        {/* Address Manager Modal */}
        {showAddressManager && (
            <div style={styles.modalOverlay}>
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
 * Login/Register Component
 */
function LoginRegisterPage({ onLogin, onRegister }) {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isLogin) {
      onLogin(email, password);
    } else {
      onRegister(email, password, firstName, lastName);
    }
  };

  return (
      <div style={styles.authContainer}>
        <div style={styles.authCard}>
          <h1 style={styles.authTitle}>E-Marketplace</h1>
          <form onSubmit={handleSubmit} style={styles.authForm}>
            <h2 style={styles.authFormTitle}>{isLogin ? 'Login' : 'Register'}</h2>

            <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                style={styles.authInput}
                required
            />

            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={styles.authInput}
                required
            />

            {!isLogin && (
                <>
                  <input
                      type="text"
                      placeholder="First Name"
                      value={firstName}
                      onChange={(e) => setFirstName(e.target.value)}
                      style={styles.authInput}
                  />

                  <input
                      type="text"
                      placeholder="Last Name"
                      value={lastName}
                      onChange={(e) => setLastName(e.target.value)}
                      style={styles.authInput}
                  />
                </>
            )}

            <button type="submit" style={styles.authButton}>
              {isLogin ? 'Login' : 'Register'}
            </button>

            <button
                type="button"
                onClick={() => setIsLogin(!isLogin)}
                style={styles.authToggle}
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
        <div style={styles.emptyCart}>
          <h2>Your cart is empty</h2>
          <p>Start shopping to add items to your cart!</p>
        </div>
    );
  }

  return (
      <div style={styles.cartContainer}>
        <h2 style={styles.sectionTitle}>Shopping Cart</h2>
        <div style={styles.cartItems}>
          {cart.items.map(item => (
              <div key={item.cartItemId} style={styles.cartItem}>
                <div style={styles.cartItemInfo}>
                  <h3 style={styles.cartItemName}>{item.productName}</h3>
                  <p style={styles.cartItemPrice}>R {item.price}</p>
                </div>
                <div style={styles.cartItemControls}>
                  <button
                      onClick={() => onUpdateQuantity(item.cartItemId, item.quantity - 1)}
                      style={styles.quantityButton}
                  >
                    −
                  </button>
                  <span style={styles.quantityDisplay}>{item.quantity}</span>
                  <button
                      onClick={() => onUpdateQuantity(item.cartItemId, item.quantity + 1)}
                      style={styles.quantityButton}
                  >
                    +
                  </button>
                  <span style={styles.cartItemSubtotal}>R {item.subtotal}</span>
                  <button
                      onClick={() => onRemove(item.cartItemId)}
                      style={styles.removeButton}
                  >
                    Remove
                  </button>
                </div>
              </div>
          ))}
        </div>
        <div style={styles.cartSummary}>
          <div style={styles.cartTotal}>
            <strong>Total: R {cart.total}</strong>
          </div>
          <button onClick={onCheckout} style={styles.checkoutButton}>
            Proceed to Checkout
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

  if (loading) return <div style={styles.loading}>Loading orders...</div>;
  if (orders.length === 0) return <div style={styles.emptyCart}>No orders yet</div>;

  return (
      <div style={styles.ordersContainer}>
        <h2 style={styles.sectionTitle}>My Orders</h2>
        {orders.map(order => (
            <div key={order.orderId} style={styles.orderCard}>
              <div style={styles.orderHeader}>
                <h3>Order {order.orderId.substring(0, 8)}</h3>
                <span style={styles.orderStatus}>{order.status}</span>
              </div>
              <p style={styles.orderDate}>
                {new Date(order.createdAt).toLocaleDateString()}
              </p>
              <p style={styles.orderTotal}>Total: R {order.totalAmount}</p>
            </div>
        ))}
      </div>
  );
}

// Styles
const styles = {
  app: {
    minHeight: '100vh',
    background: '#f8fafc',
  },
  header: {
    background: '#0f766e',
    color: 'white',
    padding: '1rem 0',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  headerContent: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '0 1.5rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  logo: {
    margin: 0,
    fontSize: '1.5rem',
  },
  nav: {
    display: 'flex',
    gap: '0.5rem',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  navButton: {
    padding: '0.5rem 1rem',
    border: 'none',
    color: 'white',
    cursor: 'pointer',
    borderRadius: '4px',
    transition: 'background 0.2s',
    fontSize: '0.9rem',
  },
  logoutButton: {
    padding: '0.5rem 1rem',
    border: 'none',
    background: '#dc2626',
    color: 'white',
    cursor: 'pointer',
    borderRadius: '4px',
    fontSize: '0.9rem',
  },
  statusBanner: {
    color: 'white',
    padding: '1rem',
    textAlign: 'center',
    animation: 'slideIn 0.3s ease',
  },
  main: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '2rem 1.5rem',
  },
  section: {
    marginBottom: '2rem',
  },
  sectionTitle: {
    fontSize: '2rem',
    marginBottom: '1.5rem',
    color: '#1e293b',
  },
  productGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
    gap: '1.5rem',
  },
  authContainer: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #0f766e 0%, #14b8a6 100%)',
  },
  authCard: {
    background: 'white',
    padding: '2rem',
    borderRadius: '12px',
    boxShadow: '0 10px 40px rgba(0,0,0,0.1)',
    width: '100%',
    maxWidth: '400px',
  },
  authTitle: {
    textAlign: 'center',
    color: '#0f766e',
    marginBottom: '1.5rem',
  },
  authForm: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
  },
  authFormTitle: {
    fontSize: '1.5rem',
    marginBottom: '1rem',
    color: '#1e293b',
  },
  authInput: {
    padding: '0.75rem',
    border: '1px solid #e2e8f0',
    borderRadius: '6px',
    fontSize: '1rem',
  },
  authButton: {
    padding: '0.75rem',
    background: '#0f766e',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    fontSize: '1rem',
    cursor: 'pointer',
  },
  authToggle: {
    padding: '0.5rem',
    background: 'transparent',
    color: '#0f766e',
    border: 'none',
    cursor: 'pointer',
    textDecoration: 'underline',
  },
  emptyCart: {
    textAlign: 'center',
    padding: '3rem 1rem',
    color: '#64748b',
  },
  cartContainer: {
    background: 'white',
    borderRadius: '12px',
    padding: '2rem',
  },
  cartItems: {
    marginBottom: '2rem',
  },
  cartItem: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1rem',
    borderBottom: '1px solid #e2e8f0',
    flexWrap: 'wrap',
  },
  cartItemInfo: {
    flex: 1,
  },
  cartItemName: {
    margin: '0 0 0.5rem 0',
    color: '#1e293b',
  },
  cartItemPrice: {
    margin: 0,
    color: '#64748b',
  },
  cartItemControls: {
    display: 'flex',
    gap: '1rem',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  quantityButton: {
    width: '32px',
    height: '32px',
    border: '1px solid #e2e8f0',
    background: 'white',
    cursor: 'pointer',
    borderRadius: '4px',
  },
  quantityDisplay: {
    minWidth: '30px',
    textAlign: 'center',
  },
  cartItemSubtotal: {
    fontWeight: 'bold',
    minWidth: '80px',
  },
  removeButton: {
    padding: '0.5rem 1rem',
    background: '#ef4444',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  cartSummary: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingTop: '1rem',
    borderTop: '2px solid #e2e8f0',
    flexWrap: 'wrap',
    gap: '1rem',
  },
  cartTotal: {
    fontSize: '1.5rem',
    color: '#0f766e',
  },
  checkoutButton: {
    padding: '0.75rem 2rem',
    background: '#0f766e',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    fontSize: '1rem',
    cursor: 'pointer',
  },
  ordersContainer: {
    background: 'white',
    borderRadius: '12px',
    padding: '2rem',
  },
  orderCard: {
    padding: '1.5rem',
    borderBottom: '1px solid #e2e8f0',
  },
  orderHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '0.5rem',
  },
  orderStatus: {
    background: '#10b981',
    color: 'white',
    padding: '0.25rem 0.75rem',
    borderRadius: '12px',
    fontSize: '0.875rem',
  },
  orderDate: {
    color: '#64748b',
    margin: '0.5rem 0',
  },
  orderTotal: {
    fontWeight: 'bold',
    color: '#0f766e',
    margin: 0,
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000,
  },
};