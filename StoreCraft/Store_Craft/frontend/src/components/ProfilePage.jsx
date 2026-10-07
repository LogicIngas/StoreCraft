import { apiUrl } from '../api.js';
import React, { useState, useEffect } from 'react';
import AddressManager from '../AddressManager';
import OrdersPage from './OrdersPage';

export default function ProfilePage({
  currentUser,
  onUpdateProfile,
  cart,
  wishlist,
  setCurrentPage,
  loadDefaultAddress
}) {
  const [activeTab, setActiveTab] = useState('details'); // 'details' | 'addresses' | 'orders'
  const [firstName, setFirstName] = useState(currentUser?.firstName || '');
  const [lastName, setLastName] = useState(currentUser?.lastName || '');
  const [email, setEmail] = useState(currentUser?.email || '');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [orderCount, setOrderCount] = useState(0);
  const [loadingOrders, setLoadingOrders] = useState(false);
  const [formMessage, setFormMessage] = useState('');
  const [formMessageType, setFormMessageType] = useState(''); // 'success' | 'error'
  const [isSaving, setIsSaving] = useState(false);

  // Fetch orders count
  useEffect(() => {
    if (currentUser?.userId) {
      const fetchOrders = async () => {
        try {
          setLoadingOrders(true);
          const response = await fetch(apiUrl(`/order/user/${currentUser.userId}`), {
            credentials: 'include',
          });
          if (response.ok) {
            const data = await response.json();
            setOrderCount(data?.length || 0);
          }
        } catch (error) {
          console.error('Error fetching orders count on profile:', error);
        } finally {
          setLoadingOrders(false);
        }
      };
      fetchOrders();
    }
  }, [currentUser]);

  const showMessage = (msg, type = 'error') => {
    setFormMessage(msg);
    setFormMessageType(type);
    setTimeout(() => setFormMessage(''), 5000);
  };

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    if (!firstName.trim() || !lastName.trim() || !email.trim()) {
      showMessage('Please fill in all required fields.', 'error');
      return;
    }

    if (password && password !== confirmPassword) {
      showMessage('Passwords do not match.', 'error');
      return;
    }

    setIsSaving(true);
    try {
      const success = await onUpdateProfile({
        firstName,
        lastName,
        email,
        password: password || undefined
      });
      if (success) {
        showMessage('Profile updated successfully!', 'success');
        setPassword('');
        setConfirmPassword('');
      } else {
        showMessage('Failed to update profile. Email might already be taken.', 'error');
      }
    } catch (error) {
      console.error(error);
      showMessage('An error occurred while saving changes.', 'error');
    } finally {
      setIsSaving(false);
    }
  };

  // Get initials for avatar
  const getInitials = () => {
    const first = firstName ? firstName.charAt(0).toUpperCase() : '';
    const last = lastName ? lastName.charAt(0).toUpperCase() : '';
    return first + last || 'U';
  };

  return (
    <div className="profile-container animated fadeIn">
      {/* Profile Header Card */}
      <div className="profile-header-card glass">
        <div className="profile-avatar-wrapper">
          <div className="profile-avatar">
            {getInitials()}
          </div>
          <span className="profile-role-tag">{currentUser?.roleName || 'BUYER'}</span>
        </div>
        
        <div className="profile-user-summary">
          <h2 className="profile-fullname">{firstName} {lastName}</h2>
          <p className="profile-email">📧 {email}</p>
          <p className="profile-joined">✨ Member of StoreCraft</p>
        </div>
      </div>

      {/* Stats Summary Grid */}
      <div className="profile-stats-grid">
        <div className="profile-stat-card glass hover-lift" onClick={() => setCurrentPage('cart')}>
          <div className="stat-icon">🛒</div>
          <div className="stat-content">
            <span className="stat-value">{cart?.items?.length || 0}</span>
            <span className="stat-label">Items in Cart</span>
          </div>
        </div>

        <div className="profile-stat-card glass hover-lift" onClick={() => setCurrentPage('wishlist')}>
          <div className="stat-icon">❤️</div>
          <div className="stat-content">
            <span className="stat-value">{wishlist?.length || 0}</span>
            <span className="stat-label">Wishlist Items</span>
          </div>
        </div>

        <div className="profile-stat-card glass hover-lift" onClick={() => setActiveTab('orders')}>
          <div className="stat-icon">📋</div>
          <div className="stat-content">
            <span className="stat-value">{loadingOrders ? '...' : orderCount}</span>
            <span className="stat-label">Orders Placed</span>
          </div>
        </div>
      </div>

      {/* Main Content Area with Navigation Tabs */}
      <div className="profile-main-layout glass">
        <div className="profile-sidebar-tabs">
          <button 
            className={`sidebar-tab-btn ${activeTab === 'details' ? 'active' : ''}`}
            onClick={() => setActiveTab('details')}
          >
            👤 Account Details
          </button>
          <button 
            className={`sidebar-tab-btn ${activeTab === 'addresses' ? 'active' : ''}`}
            onClick={() => setActiveTab('addresses')}
          >
            📍 Saved Addresses
          </button>
          <button 
            className={`sidebar-tab-btn ${activeTab === 'orders' ? 'active' : ''}`}
            onClick={() => setActiveTab('orders')}
          >
            📋 Order History
          </button>
        </div>

        <div className="profile-content-panel">
          {/* Tab 1: Account Details Form */}
          {activeTab === 'details' && (
            <div className="tab-pane animated slideUp">
              <h3 className="pane-title">Account Information</h3>
              {formMessage && (
                <div className={`form-feedback-message ${formMessageType}`}>
                  {formMessage}
                </div>
              )}
              <form onSubmit={handleProfileSubmit} className="profile-details-form">
                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="p-first-name">First Name *</label>
                    <input
                      id="p-first-name"
                      type="text"
                      value={firstName}
                      onChange={(e) => setFirstName(e.target.value)}
                      className="form-input"
                      placeholder="Enter first name"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="p-last-name">Last Name *</label>
                    <input
                      id="p-last-name"
                      type="text"
                      value={lastName}
                      onChange={(e) => setLastName(e.target.value)}
                      className="form-input"
                      placeholder="Enter last name"
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="p-email">Email Address *</label>
                  <input
                    id="p-email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="form-input"
                    placeholder="Enter email address"
                    required
                  />
                </div>

                <hr className="form-divider" />
                <h4 className="form-subtitle">Change Password (Leave blank to keep current)</h4>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="p-password">New Password</label>
                    <input
                      id="p-password"
                      type="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="form-input"
                      placeholder="New password"
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="p-confirm-password">Confirm Password</label>
                    <input
                      id="p-confirm-password"
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="form-input"
                      placeholder="Confirm new password"
                    />
                  </div>
                </div>

                <button type="submit" className="save-profile-btn" disabled={isSaving}>
                  {isSaving ? 'Saving Changes...' : '💾 Save Details'}
                </button>
              </form>
            </div>
          )}

          {/* Tab 2: Saved Addresses Inline */}
          {activeTab === 'addresses' && (
            <div className="tab-pane animated slideUp">
              <AddressManager
                userId={currentUser?.userId}
                onClose={() => {
                  setActiveTab('details');
                  if (loadDefaultAddress) loadDefaultAddress(currentUser?.userId);
                }}
                standalone={false}
              />
            </div>
          )}

          {/* Tab 3: Order History */}
          {activeTab === 'orders' && (
            <div className="tab-pane animated slideUp">
              <OrdersPage userId={currentUser?.userId} />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
