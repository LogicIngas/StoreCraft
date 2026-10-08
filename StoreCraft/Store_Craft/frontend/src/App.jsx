import { apiUrl } from './api.js';
import React, { useState, useEffect } from 'react';
import { supabase } from './supabaseClient';
import { supabase } from './supabaseClient';
import './App.css';
import './landing-and-footer.css';

// Components
import Navbar from './components/Navbar';
import LoginRegisterPage from './components/LoginRegisterPage';
import Storefront from './components/Storefront';
import CartPage from './components/CartPage';
import WishlistPage from './components/WishlistPage';
import OrdersPage from './components/OrdersPage';
import UploadProduct from './components/UploadProduct';
import AddressManager from './AddressManager';
import PaymentCheckout from './PaymentCheckout';
import StatusBanner from './components/StatusBanner';
import Footer from './Footer';
import LandingPage from './LandingPage';
import AdminDashboard from './AdminDashboard';
import ProfilePage from './components/ProfilePage';
import CookieConsent from './components/CookieConsent';

export default function App() {
  // State
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
  // Cookie consent state
  const [cookieAccepted, setCookieAccepted] = useState(
    localStorage.getItem('cookieConsentAccepted') === 'accepted'
  );

  // Force landing page when consent not given
  useEffect(() => {
    if (!cookieAccepted) {
      setCurrentPage('landing');
    }
  }, [cookieAccepted]);
  const [showCheckout, setShowCheckout] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [loading, setLoading] = useState(false);

  // Load user from localStorage on mount
  useEffect(() => {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const user = JSON.parse(savedUser);
      setCurrentUser(user);
      loadUserData(user.userId);
      loadDefaultAddress(user.userId);
      // If admin, default to admin dashboard
      if (user.roleName === 'ADMIN') {
        setCurrentPage('admin');
      }
    }
  }, []);

  // Load products on mount
  useEffect(() => {
    loadProducts();
  }, []);

  // Supabase auth state listener
  useEffect(() => {
    const {
      data: { subscription },
    } = supabase.auth.onAuthStateChange(async (event, session) => {
      if (event === 'SIGNED_IN' && session?.user) {
        const supaUser = session.user;
        const fakeBackendUser = {
          userId: supaUser.id,
          email: supaUser.email,
          firstName: supaUser.user_metadata?.full_name?.split(' ')[0] || supaUser.email.split('@')[0],
          lastName: supaUser.user_metadata?.full_name?.split(' ')[1] || '',
          roleName: 'BUYER', 
          authProvider: 'supabase'
        };
        setCurrentUser(fakeBackendUser);
        localStorage.setItem('currentUser', JSON.stringify(fakeBackendUser));
        await loadUserData(fakeBackendUser.userId);
        await loadDefaultAddress(fakeBackendUser.userId);
        setCurrentPage('storefront');
        showStatus('✅ Login successful via Supabase!', 'success');
      } else if (event === 'SIGNED_OUT') {
        setCurrentUser(null);
        setCart(null);
        setWishlist([]);
        setShippingAddress('');
        localStorage.removeItem('currentUser');
        setCurrentPage('storefront');
      }
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  // Filter products
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

  // ---------- API Calls ----------
  const loadProducts = async () => {
    try {
      const response = await fetch(apiUrl('/product/all'), {
        credentials: 'include',
      });
      const data = await response.json();
      setProducts(data);
      const uniqueCategories = ['all', ...new Set(data.map(p => p.category))];
      setCategories(uniqueCategories);
    } catch (error) {
      console.error('Error loading products:', error);
    }
  };

  const loadUserData = async (userId) => {
    try {
      const cartResponse = await fetch(apiUrl(`/cart/${userId}`), {
        credentials: 'include',
      });
      if (cartResponse.ok) {
        const cartData = await cartResponse.json();
        setCart(cartData);
      } else {
        setCart({ cartId: '', userId, items: [], total: 0 });
      }

      const wishlistResponse = await fetch(apiUrl(`/api/wishlist/user/${userId}`), {
        credentials: 'include',
      });
      if (wishlistResponse.ok) {
        const wishlistData = await wishlistResponse.json();
        setWishlist(wishlistData);
      }
    } catch (error) {
      console.error('Error loading user data:', error);
    }
  };

  const loadDefaultAddress = async (userId) => {
    try {
      const response = await fetch(apiUrl(`/address/user/${userId}/default`), {
        credentials: 'include',
      });
      if (response.ok) {
        const address = await response.json();
        if (address) {
          const formatted = `${address.streetAddress}, ${address.city}, ${address.stateProvince || ''} ${address.postalCode}, ${address.country}`;
          setShippingAddress(formatted);
        } else {
          setShippingAddress('');
        }
      } else {
        setShippingAddress('');
      }
    } catch (error) {
      console.error('Error loading default address:', error);
      setShippingAddress('');
    }
  };

  // ---------- Handlers ----------
  const handleLogin = async (email, password) => {
    try {
      setLoading(true);
      const response = await fetch(apiUrl('/user/login'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email, password }),
      });
      const data = await response.json();
      if (!response.ok) {
        showStatus(data.message || 'Login failed', 'error');
        return;
      }
      setCurrentUser(data);
      localStorage.setItem('currentUser', JSON.stringify(data));
      await loadUserData(data.userId);
      await loadDefaultAddress(data.userId);
      if (data.roleName === 'ADMIN') {
        setCurrentPage('admin');
      } else {
        setCurrentPage('storefront');
      }
      showStatus('✅ Login successful! Welcome to StoreCraft!', 'success');
    } catch (error) {
      showStatus('❌ Login failed. Please try again.', 'error');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (email, password, firstName, lastName, roleName) => {
    try {
      setLoading(true);
      const response = await fetch(apiUrl('/user/create'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email, password, firstName, lastName, roleName }),
      });
      const data = await response.json();
      if (!response.ok) {
        showStatus(data.message || 'Registration failed', 'error');
        return;
      }
      setCurrentUser(data);
      localStorage.setItem('currentUser', JSON.stringify(data));
      await loadUserData(data.userId);
      await loadDefaultAddress(data.userId);
      if (data.roleName === 'ADMIN') {
        setCurrentPage('admin');
      } else {
        setCurrentPage('storefront');
      }
      showStatus('✅ Account created! Welcome to StoreCraft!', 'success');
    } catch (error) {
      showStatus('❌ Registration failed. Please try again.', 'error');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    // Call the backend logout to clear the server-side cookie
    try {
      await fetch(apiUrl('/user/logout'), {
        method: 'POST',
        credentials: 'include',
      });
    } catch (error) {
      console.error('Logout request failed:', error);
    }
    
    try {
      await supabase.auth.signOut();
    } catch (error) {
      console.error('Supabase logout failed:', error);
    }
    setCurrentUser(null);
    setCart(null);
    setWishlist([]);
    setShippingAddress('');
    localStorage.removeItem('currentUser');
    setCurrentPage('storefront');
    showStatus('✅ Logged out successfully!', 'success');
  };

  const handleUpdateProfile = async (profileData) => {
    if (!currentUser) return false;
    try {
      setLoading(true);
      const response = await fetch(apiUrl(`/user/update/${currentUser.userId}`), {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(profileData),
      });
      const data = await response.json();
      if (!response.ok) {
        showStatus(data.message || 'Profile update failed', 'error');
        return false;
      }
      const updatedUser = { ...currentUser, ...data };
      setCurrentUser(updatedUser);
      localStorage.setItem('currentUser', JSON.stringify(updatedUser));
      showStatus('✅ Profile updated successfully!', 'success');
      return true;
    } catch (error) {
      showStatus('❌ Profile update failed. Please try again.', 'error');
      console.error(error);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (productId) => {
    if (!currentUser) {
      showStatus('❌ Please log in to add items to cart', 'error');
      return;
    }
    if (currentUser.roleName === 'ADMIN') {
      showStatus('❌ Admins cannot add items to cart', 'error');
      return;
    }
    try {
      const response = await fetch(apiUrl('/cart/add'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ userId: currentUser.userId, productId, quantity: 1 }),
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
      console.error(error);
    }
  };

  const handleRemoveFromCart = async (cartItemId) => {
    if (!currentUser) return;
    try {
      const response = await fetch(apiUrl(`/cart/remove/${cartItemId}?userId=${currentUser.userId}`), {
        method: 'DELETE',
        credentials: 'include',
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
      const response = await fetch(apiUrl(`/cart/update/${cartItemId}`), {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ userId: currentUser.userId, quantity }),
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
    if (currentUser.roleName === 'ADMIN') {
      showStatus('❌ Admins cannot use wishlist', 'error');
      return;
    }
    try {
      const response = await fetch(apiUrl('/api/wishlist/add'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ userId: currentUser.userId, productId }),
      });
      if (response.ok) {
        const updatedWishlist = await fetch(apiUrl(`/api/wishlist/user/${currentUser.userId}`), {
          credentials: 'include',
        });
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
      const response = await fetch(apiUrl(`/api/wishlist/remove/${wishlistId}`), {
        method: 'DELETE',
        credentials: 'include',
      });
      if (response.ok) {
        setWishlist(wishlist.filter(w => w.wishlistId !== wishlistId));
        showStatus('✅ Removed from wishlist', 'success');
      }
    } catch (error) {
      console.error('Error removing from wishlist:', error);
    }
  };

  const handleCheckout = async (cardToken, cardLast4, cardHolderName, saveAddress, addressData) => {
    if (!currentUser || !cart) {
      showStatus('❌ Cart is empty', 'error');
      return;
    }
    if (!shippingAddress.trim()) {
      showStatus('❌ Shipping address is required', 'error');
      return;
    }

    try {
      setIsProcessing(true);
      if (saveAddress && addressData) {
        await saveShippingAddress(addressData);
      }

      const response = await fetch(apiUrl('/payment/checkout'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          userId: currentUser.userId,
          shippingAddress: shippingAddress,
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
      console.error(error);
    } finally {
      setIsProcessing(false);
    }
  };

  const saveShippingAddress = async (addressData) => {
    try {
      const payload = {
        userId: currentUser.userId,
        type: 'SHIPPING',
        recipientName: addressData.recipientName || currentUser.firstName + ' ' + currentUser.lastName,
        phoneNumber: addressData.phoneNumber || '',
        streetAddress: addressData.streetAddress || shippingAddress.split(',')[0].trim(),
        city: addressData.city || shippingAddress.split(',')[1]?.trim() || '',
        stateProvince: addressData.stateProvince || '',
        postalCode: addressData.postalCode || '',
        country: addressData.country || 'South Africa',
        isDefault: true,
      };
      const response = await fetch(apiUrl('/address/create'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
      });
      if (response.ok) {
        showStatus('✅ Address saved successfully!', 'success');
        await loadDefaultAddress(currentUser.userId);
      }
    } catch (error) {
      console.error('Error saving address:', error);
    }
  };

  const showStatus = (message, type = 'info') => {
    setStatusMessage(message);
    setStatusType(type);
    setTimeout(() => setStatusMessage(''), 4000);
  };

  // Cookie consent callbacks
  const handleCookieAccept = () => {
    setCookieAccepted(true);
  };

  const handleCookieDecline = () => {
    setCookieAccepted(false);
    // Force user to landing page only
    setCurrentPage('landing');
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
  };

  // ---------- Render ----------
  if (!currentUser) {
    return (
      <div className="app-wrapper">
        <StatusBanner message={statusMessage} type={statusType} />
        <LandingPage onLoginClick={() => setCurrentPage('login')} products={products} />
        {currentPage === 'login' && (
          <div className="modal-overlay" onClick={() => setCurrentPage('landing')}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
              <button className="modal-close" onClick={() => setCurrentPage('landing')}>✕</button>
              <LoginRegisterPage onLogin={handleLogin} onRegister={handleRegister} />
            </div>
          </div>
        )}
        <Footer />
        <CookieConsent onAccept={handleCookieAccept} onDecline={handleCookieDecline} />
      </div>
    );
  }

  const isAdmin = currentUser.roleName === 'ADMIN';
  const isSeller = currentUser.roleName === 'SELLER';

  return (
    <div className="app-wrapper">
      <StatusBanner message={statusMessage} type={statusType} />
      <Navbar
        currentUser={currentUser}
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        cartCount={cart?.items?.length || 0}
        wishlistCount={wishlist?.length || 0}
        handleLogout={handleLogout}
        isSeller={isSeller}
        isAdmin={isAdmin}
      />
      <main className="main-content">
        {currentPage === 'admin' && isAdmin && (
          <AdminDashboard userId={currentUser.userId} />
        )}

        {currentPage === 'profile' && (
          <ProfilePage
            currentUser={currentUser}
            onUpdateProfile={handleUpdateProfile}
            cart={cart}
            wishlist={wishlist}
            setCurrentPage={setCurrentPage}
            loadDefaultAddress={loadDefaultAddress}
          />
        )}

        {currentPage === 'storefront' && !isAdmin && (
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
            isAdmin={isAdmin}
          />
        )}

        {currentPage === 'cart' && !isAdmin && (
          <CartPage
            cart={cart}
            onRemove={handleRemoveFromCart}
            onUpdateQuantity={handleUpdateQuantity}
            onCheckout={() => setShowCheckout(true)}
          />
        )}

        {currentPage === 'wishlist' && !isAdmin && (
          <WishlistPage
            wishlist={wishlist}
            onAddToCart={handleAddToCart}
            onRemoveFromWishlist={handleRemoveFromWishlist}
            setCurrentPage={setCurrentPage}
          />
        )}

        {currentPage === 'orders' && !isAdmin && (
          <OrdersPage userId={currentUser.userId} />
        )}

        {currentPage === 'upload' && isSeller && (
          <UploadProduct onProductAdded={() => loadProducts()} />
        )}

        {currentPage === 'addresses' && (
          <AddressManager
            userId={currentUser.userId}
            onClose={() => {
              setCurrentPage(isAdmin ? 'admin' : 'storefront');
              loadDefaultAddress(currentUser.userId);
            }}
            standalone={true}
          />
        )}

        {showCheckout && !isAdmin && (
          <PaymentCheckout
            cart={cart}
            shippingAddress={shippingAddress}
            onShippingAddressChange={setShippingAddress}
            onPayment={handleCheckout}
            onClose={() => setShowCheckout(false)}
            isProcessing={isProcessing}
            userId={currentUser.userId}
            onAddressSaved={loadDefaultAddress}
          />
        )}
      </main>
      <Footer />
    </div>
  );
}