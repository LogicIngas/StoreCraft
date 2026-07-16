import React, { useState, useEffect } from 'react';
import './App.css';
import './landing-and-footer.css';
import Navbar from './components/Navbar';
import LoginRegisterPage from './components/LoginRegisterPage';
import Storefront from './components/Storefront';
import CartPage from './components/CartPage';
import WishlistPage from './components/WishlistPage';
import OrdersPage from './components/OrdersPage';
import UploadProduct from './components/UploadProduct';
import AddressManager from './AddressManager';
import PaymentCheckout from './PaymentCheckout';
import ProductCard from './components/ProductCard';
import StatusBanner from './components/StatusBanner';
import Footer from './Footer';
import LandingPage from './LandingPage';
export default function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [currentPage, setCurrentPage] = useState('storefront');
  const [cart, setCart] = useState(null);
  const [wishlist, setWishlist] = useState([]);
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [categories, setCategories] = useState(['all']);
  const [statusMessage, setStatusMessage] = useState('');
  const [statusType, setStatusType] = useState('');
  const [shippingAddress, setShippingAddress] = useState('');
  const [showCheckout, setShowCheckout] = useState(false);
  const [showAddressManager, setShowAddressManager] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [loading, setLoading] = useState(false);

  // ========== USEEFFECTS ==========

  // Load user from localStorage on mount
  useEffect(() => {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const user = JSON.parse(savedUser);
      setCurrentUser(user);
      loadUserData(user.userId);
    }
  }, []);

  // Load products on mount
  useEffect(() => {
    loadProducts();
  }, []);

  // Filter products based on search and category
  useEffect(() => {
    let filtered = products;

    if (selectedCategory !== 'all') {
      filtered = filtered.filter(p => p.category === selectedCategory);
    }

    if (searchTerm.trim()) {
      filtered = filtered.filter(p =>
          p.name.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    setFilteredProducts(filtered);
  }, [products, searchTerm, selectedCategory]);

  // ========== API CALLS ==========

  const loadProducts = async () => {
    try {
      const response = await fetch('http://localhost:8080/product/all');
      const data = await response.json();
      setProducts(data);

      // Extract unique categories
      const uniqueCategories = ['all', ...new Set(data.map(p => p.category))];
      setCategories(uniqueCategories);
    } catch (error) {
      console.error('Error loading products:', error);
    }
  };

  const loadUserData = async (userId) => {
    try {
      // Load cart
      const cartResponse = await fetch(`http://localhost:8080/cart/${userId}`);
      if (cartResponse.ok) {
        const cartData = await cartResponse.json();
        setCart(cartData);
      } else {
        // Create new cart if doesn't exist
        const newCart = { cartId: '', userId, items: [], total: 0 };
        setCart(newCart);
      }

      // Load wishlist
      const wishlistResponse = await fetch(`http://localhost:8080/api/wishlist/user/${userId}`);
      if (wishlistResponse.ok) {
        const wishlistData = await wishlistResponse.json();
        setWishlist(wishlistData);
      }
    } catch (error) {
      console.error('Error loading user data:', error);
    }
  };

  // ========== HANDLERS ==========

  const handleLogin = async (email, password) => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        showStatus(data, 'error');
        return;
      }

      setCurrentUser(data);
      localStorage.setItem('currentUser', JSON.stringify(data));
      loadUserData(data.userId);
      setCurrentPage('storefront');
      showStatus('✅ Login successful! Welcome to AfriConnect!', 'success');
    } catch (error) {
      showStatus('❌ Login failed. Please try again.', 'error');
      console.error('Login error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (email, password, firstName, lastName, roleName) => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/user/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, firstName, lastName, roleName }),
      });

      const data = await response.json();

      if (!response.ok) {
        showStatus(data, 'error');
        return;
      }

      setCurrentUser(data);
      localStorage.setItem('currentUser', JSON.stringify(data));
      loadUserData(data.userId);
      setCurrentPage('storefront');
      showStatus('✅ Account created! Welcome to AfriConnect!', 'success');
    } catch (error) {
      showStatus('❌ Registration failed. Please try again.', 'error');
      console.error('Register error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    setCurrentUser(null);
    setCart(null);
    setWishlist([]);
    localStorage.removeItem('currentUser');
    setCurrentPage('storefront');
    showStatus('✅ Logged out successfully!', 'success');
  };

  const handleAddToCart = async (productId) => {
    if (!currentUser) {
      showStatus('❌ Please log in to add items to cart', 'error');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/cart/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          productId,
          quantity: 1,
        }),
      });

      if (response.ok) {
        const cartData = await response.json();
        setCart(cartData);
        showStatus('✅ Added to cart!', 'success');
      } else {
        showStatus('❌ Failed to add to cart', 'error');
      }
    } catch (error) {
      showStatus('❌ Error adding to cart', 'error');
      console.error('Add to cart error:', error);
    }
  };

  const handleRemoveFromCart = async (cartItemId) => {
    if (!currentUser) return;

    try {
      const response = await fetch(`http://localhost:8080/cart/remove/${cartItemId}?userId=${currentUser.userId}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        const cartData = await response.json();
        setCart(cartData);
        showStatus('✅ Removed from cart', 'success');
      }
    } catch (error) {
      console.error('Error removing from cart:', error);
    }
  };

  const handleUpdateQuantity = async (cartItemId, quantity) => {
    if (!currentUser || quantity < 1) return;

    try {
      const response = await fetch(`http://localhost:8080/cart/update/${cartItemId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          quantity,
        }),
      });

      if (response.ok) {
        const cartData = await response.json();
        setCart(cartData);
      }
    } catch (error) {
      console.error('Error updating quantity:', error);
    }
  };

  const handleAddToWishlist = async (productId) => {
    if (!currentUser) {
      showStatus('❌ Please log in to add to wishlist', 'error');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/wishlist/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          productId,
        }),
      });

      if (response.ok) {
        const updatedWishlist = await fetch(`http://localhost:8080/api/wishlist/user/${currentUser.userId}`);
        const wishlistData = await updatedWishlist.json();
        setWishlist(wishlistData);
        showStatus('✅ Added to wishlist!', 'success');
      }
    } catch (error) {
      console.error('Error adding to wishlist:', error);
    }
  };

  const handleRemoveFromWishlist = async (wishlistId) => {
    if (!currentUser) return;

    try {
      const response = await fetch(`http://localhost:8080/api/wishlist/remove/${wishlistId}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        setWishlist(wishlist.filter(w => w.wishlistId !== wishlistId));
        showStatus('✅ Removed from wishlist', 'success');
      }
    } catch (error) {
      console.error('Error removing from wishlist:', error);
    }
  };

  const handleCheckout = async (cardToken, cardLast4, cardHolderName) => {
    if (!currentUser || !cart || !shippingAddress.trim()) {
      showStatus('❌ Please provide all required information', 'error');
      return;
    }

    try {
      setIsProcessing(true);
      const response = await fetch('http://localhost:8080/payment/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          shippingAddress,
          cardToken,
          cardLast4,
          cardHolderName,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        setShowCheckout(false);
        setShippingAddress('');
        setCart(null);
        showStatus('✅ Payment successful! Order confirmed!', 'success');
        setCurrentPage('orders');
      } else {
        showStatus(`❌ ${data.message || 'Payment failed'}`, 'error');
      }
    } catch (error) {
      showStatus('❌ Payment processing failed', 'error');
      console.error('Checkout error:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const showStatus = (message, type = 'info') => {
    setStatusMessage(message);
    setStatusType(type);
    setTimeout(() => setStatusMessage(''), 4000);
  };

  // ========== RENDER ==========

  // If not logged in, show landing page
  if (!currentUser) {
    return (
        <div className="app-wrapper">
          <LandingPage
              onLoginClick={() => setCurrentPage('login')}
              products={products}
          />
          {currentPage === 'login' && (
              <div className="modal-overlay" onClick={() => setCurrentPage('storefront')}>
                <div className="modal-content" onClick={e => e.stopPropagation()}>
                  <button
                      className="modal-close"
                      onClick={() => setCurrentPage('storefront')}
                  >
                    ✕
                  </button>
                  <LoginRegisterPage
                      onLogin={handleLogin}
                      onRegister={handleRegister}
                  />
                </div>
              </div>
          )}
          <Footer />
        </div>
    );
  }

  // If logged in, show full app
  return (
      <div className="app-wrapper">
        <StatusBanner message={statusMessage} type={statusType} />
        <Navbar
            currentUser={currentUser}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            cartCount={cart?.items?.length || 0}
            wishlistCount={wishlist?.length || 0}
            setShowAddressManager={setShowAddressManager}
            handleLogout={handleLogout}
            isSeller={currentUser.roleName === 'SELLER'}
        />

        <main className="main-content">
          {currentPage === 'storefront' && (
              <Storefront
                  products={products}
                  filteredProducts={filteredProducts}
                  loading={loading}
                  searchTerm={searchTerm}
                  setSearchTerm={setSearchTerm}
                  selectedCategory={selectedCategory}
                  setSelectedCategory={setSelectedCategory}
                  categories={categories}
                  onAddToCart={handleAddToCart}
                  onAddToWishlist={handleAddToWishlist}
                  onRemoveFromWishlist={handleRemoveFromWishlist}
                  wishlist={wishlist}
              />
          )}

          {currentPage === 'cart' && (
              <CartPage
                  cart={cart}
                  onRemove={handleRemoveFromCart}
                  onUpdateQuantity={handleUpdateQuantity}
                  onCheckout={() => setShowCheckout(true)}
              />
          )}

          {currentPage === 'wishlist' && (
              <WishlistPage
                  wishlist={wishlist}
                  onAddToCart={handleAddToCart}
                  onRemoveFromWishlist={handleRemoveFromWishlist}
                  setCurrentPage={setCurrentPage}
              />
          )}

          {currentPage === 'orders' && (
              <OrdersPage userId={currentUser.userId} />
          )}

          {currentPage === 'upload' && currentUser.roleName === 'SELLER' && (
              <UploadProduct onProductAdded={() => loadProducts()} />
          )}

          {showCheckout && (
              <PaymentCheckout
                  cart={cart}
                  shippingAddress={shippingAddress}
                  onShippingAddressChange={setShippingAddress}
                  onPayment={handleCheckout}
                  onClose={() => setShowCheckout(false)}
                  isProcessing={isProcessing}
              />
          )}

          {showAddressManager && (
              <AddressManager
                  userId={currentUser.userId}
                  onClose={() => setShowAddressManager(false)}
              />
          )}
        </main>

        <Footer />
      </div>
  );
}