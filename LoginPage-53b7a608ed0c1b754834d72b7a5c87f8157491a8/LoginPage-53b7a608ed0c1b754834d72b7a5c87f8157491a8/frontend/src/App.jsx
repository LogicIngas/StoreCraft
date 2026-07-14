import React, { useState, useEffect } from 'react';
import './App.css';
import ProductCard from './components/ProductCard';
import UploadProduct from './components/UploadProduct';
import PaymentCheckout from './PaymentCheckout';
import AddressManager from './AddressManager';
import LoginRegisterPage from './components/LoginRegisterPage';
import Navbar from './components/Navbar';
import StatusBanner from './components/StatusBanner';
import Storefront from './components/Storefront';
import CartPage from './components/CartPage';
import OrdersPage from './components/OrdersPage';
import WishlistPage from './components/WishlistPage';

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
        <Navbar
            currentUser={currentUser}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            cartCount={cartCount}
            wishlistCount={wishlist.length}
            setShowAddressManager={setShowAddressManager}
            handleLogout={handleLogout}
            isSeller={isSeller}
        />

        {/* Status Message */}
        <StatusBanner message={statusMessage} type={statusType} />

        {/* Main Content */}
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

          {currentPage === 'wishlist' && (
              <WishlistPage
                  wishlist={wishlist}
                  onAddToCart={handleAddToCart}
                  onRemoveFromWishlist={handleRemoveFromWishlistById}
                  setCurrentPage={setCurrentPage}
              />
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