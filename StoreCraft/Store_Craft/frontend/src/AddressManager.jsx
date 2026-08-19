import React, { useState, useEffect } from 'react';

export default function AddressManager({ userId, onClose, standalone = false }) {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const [formData, setFormData] = useState({
    type: 'SHIPPING',
    recipientName: '',
    phoneNumber: '',
    streetAddress: '',
    city: '',
    stateProvince: '',
    postalCode: '',
    country: 'South Africa',
    isDefault: false,
  });

  const showStatus = (msg, type = 'error') => {
    setMessage(msg);
    setMessageType(type);
    setTimeout(() => setMessage(''), 4000);
  };

  useEffect(() => {
    loadAddresses();
  }, [userId]);

  const loadAddresses = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/address/user/${userId}`);
      if (response.ok) {
        const data = await response.json();
        setAddresses(data);
      }
    } catch (error) {
      console.error('Error loading addresses:', error);
      showStatus('Failed to load addresses');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.recipientName.trim() || !formData.streetAddress.trim() ||
        !formData.city.trim() || !formData.postalCode.trim() || !formData.country.trim()) {
      showStatus('Please fill in all required fields');
      return;
    }

    try {
      const url = editingId
        ? `http://localhost:8080/address/${editingId}`
        : 'http://localhost:8080/address/create';
      const method = editingId ? 'PUT' : 'POST';
      const payload = editingId ? formData : { userId, ...formData };

      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const data = await response.json();
        showStatus(data.message || 'Failed to save address');
        return;
      }

      showStatus(editingId ? 'Address updated!' : 'Address created!', 'success');
      setFormData({
        type: 'SHIPPING',
        recipientName: '',
        phoneNumber: '',
        streetAddress: '',
        city: '',
        stateProvince: '',
        postalCode: '',
        country: 'South Africa',
        isDefault: false,
      });
      setEditingId(null);
      setShowForm(false);
      loadAddresses();
    } catch (error) {
      console.error('Error saving address:', error);
      showStatus('Error saving address');
    }
  };

  const handleEdit = (address) => {
    setFormData({
      type: address.type,
      recipientName: address.recipientName,
      phoneNumber: address.phoneNumber,
      streetAddress: address.streetAddress,
      city: address.city,
      stateProvince: address.stateProvince,
      postalCode: address.postalCode,
      country: address.country,
      isDefault: address.isDefault,
    });
    setEditingId(address.addressId);
    setShowForm(true);
  };

  const handleDelete = async (addressId) => {
    if (!window.confirm('Are you sure you want to delete this address?')) return;
    try {
      const response = await fetch(`http://localhost:8080/address/${addressId}`, {
        method: 'DELETE',
      });
      if (!response.ok) {
        showStatus('Failed to delete address');
        return;
      }
      showStatus('Address deleted!', 'success');
      loadAddresses();
    } catch (error) {
      console.error('Error deleting address:', error);
      showStatus('Error deleting address');
    }
  };

  const handleSetDefault = async (addressId) => {
    try {
      const response = await fetch(`http://localhost:8080/address/${addressId}/default`, {
        method: 'PUT',
      });
      if (!response.ok) {
        showStatus('Failed to set default');
        return;
      }
      showStatus('Default address updated!', 'success');
      loadAddresses();
    } catch (error) {
      console.error('Error setting default:', error);
      showStatus('Error setting default');
    }
  };

  // Page styles (no overlay)
  const pageStyles = {
    container: {
      background: 'white',
      borderRadius: '12px',
      padding: '2rem',
      maxWidth: '900px',
      margin: '0 auto',
      boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
    },
    header: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: '1.5rem',
      borderBottom: '1px solid #e2e8f0',
      paddingBottom: '1rem',
    },
    title: {
      margin: 0,
      fontSize: '1.75rem',
      color: '#1e293b',
    },
    closeButton: {
      background: 'none',
      border: 'none',
      fontSize: '1.5rem',
      cursor: 'pointer',
      color: '#64748b',
      padding: '0.5rem',
    },
    message: {
      color: 'white',
      padding: '1rem',
      borderRadius: '6px',
      marginBottom: '1rem',
      textAlign: 'center',
      fontWeight: '500',
    },
    loading: {
      textAlign: 'center',
      padding: '2rem',
      color: '#64748b',
    },
    addButton: {
      padding: '0.75rem 1.5rem',
      background: '#0f766e',
      color: 'white',
      border: 'none',
      borderRadius: '6px',
      cursor: 'pointer',
      fontWeight: '600',
      marginBottom: '1.5rem',
    },
    emptyState: {
      textAlign: 'center',
      padding: '3rem 1rem',
      color: '#64748b',
    },
    addressList: {
      display: 'flex',
      flexDirection: 'column',
      gap: '1rem',
    },
    addressCard: {
      border: '1px solid #e2e8f0',
      borderRadius: '8px',
      padding: '1.5rem',
      background: '#f8fafc',
    },
    addressHeader: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'flex-start',
      marginBottom: '1rem',
      gap: '1rem',
    },
    recipientName: {
      margin: '0 0 0.5rem 0',
      fontSize: '1.1rem',
      color: '#1e293b',
    },
    type: {
      display: 'inline-block',
      background: '#e0f2fe',
      color: '#0369a1',
      padding: '0.25rem 0.75rem',
      borderRadius: '12px',
      fontSize: '0.75rem',
      fontWeight: '600',
      marginRight: '0.5rem',
    },
    defaultBadge: {
      display: 'inline-block',
      background: '#10b981',
      color: 'white',
      padding: '0.25rem 0.75rem',
      borderRadius: '12px',
      fontSize: '0.75rem',
      fontWeight: '600',
    },
    actions: {
      display: 'flex',
      gap: '0.5rem',
    },
    defaultButton: {
      padding: '0.5rem 1rem',
      background: '#f59e0b',
      color: 'white',
      border: 'none',
      borderRadius: '6px',
      cursor: 'pointer',
      fontSize: '0.875rem',
    },
    editButton: {
      padding: '0.5rem 1rem',
      background: '#3b82f6',
      color: 'white',
      border: 'none',
      borderRadius: '6px',
      cursor: 'pointer',
      fontSize: '0.875rem',
    },
    deleteButton: {
      padding: '0.5rem 1rem',
      background: '#ef4444',
      color: 'white',
      border: 'none',
      borderRadius: '6px',
      cursor: 'pointer',
      fontSize: '0.875rem',
    },
    addressDetails: {
      color: '#475569',
      lineHeight: '1.6',
    },
    form: {
      display: 'flex',
      flexDirection: 'column',
      gap: '1rem',
    },
    formGroup: {
      display: 'flex',
      flexDirection: 'column',
      gap: '0.5rem',
    },
    formRow: {
      display: 'grid',
      gridTemplateColumns: '1fr 1fr',
      gap: '1rem',
    },
    input: {
      padding: '0.75rem',
      border: '1px solid #e2e8f0',
      borderRadius: '6px',
      fontSize: '0.875rem',
    },
    checkboxGroup: {
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
    },
    checkboxLabel: {
      fontSize: '0.875rem',
      color: '#1e293b',
      cursor: 'pointer',
    },
    formButtons: {
      display: 'flex',
      gap: '1rem',
      marginTop: '1rem',
    },
    cancelButton: {
      flex: 1,
      padding: '0.75rem',
      background: '#e2e8f0',
      color: '#1e293b',
      border: 'none',
      borderRadius: '6px',
      cursor: 'pointer',
      fontWeight: '600',
    },
    submitButton: {
      flex: 1,
      padding: '0.75rem',
      background: '#0f766e',
      color: 'white',
      border: 'none',
      borderRadius: '6px',
      cursor: 'pointer',
      fontWeight: '600',
    },
  };

  return (
    <div style={pageStyles.container}>
      <div style={pageStyles.header}>
        <h2 style={pageStyles.title}>Manage Addresses</h2>
        <button onClick={onClose} style={pageStyles.closeButton}>✕</button>
      </div>

      {message && (
        <div style={{
          ...pageStyles.message,
          background: messageType === 'success' ? '#10b981' : '#ef4444',
        }}>
          {message}
        </div>
      )}

      {loading && <div style={pageStyles.loading}>Loading addresses...</div>}

      {!showForm && !loading && (
        <div>
          <button onClick={() => setShowForm(true)} style={pageStyles.addButton}>
            + Add New Address
          </button>

          {addresses.length === 0 ? (
            <div style={pageStyles.emptyState}>
              <p>No addresses saved yet.</p>
              <p>Click "Add New Address" to get started.</p>
            </div>
          ) : (
            <div style={pageStyles.addressList}>
              {addresses.map((address) => (
                <div key={address.addressId} style={pageStyles.addressCard}>
                  <div style={pageStyles.addressHeader}>
                    <div>
                      <h3 style={pageStyles.recipientName}>{address.recipientName}</h3>
                      <span style={pageStyles.type}>{address.type}</span>
                      {address.isDefault && (
                        <span style={pageStyles.defaultBadge}>DEFAULT</span>
                      )}
                    </div>
                    <div style={pageStyles.actions}>
                      {!address.isDefault && (
                        <button
                          onClick={() => handleSetDefault(address.addressId)}
                          style={pageStyles.defaultButton}
                        >
                          Set Default
                        </button>
                      )}
                      <button
                        onClick={() => handleEdit(address)}
                        style={pageStyles.editButton}
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(address.addressId)}
                        style={pageStyles.deleteButton}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                  <div style={pageStyles.addressDetails}>
                    <p>{address.streetAddress}</p>
                    <p>
                      {address.city}
                      {address.stateProvince && `, ${address.stateProvince}`}
                      {address.postalCode && ` ${address.postalCode}`}
                    </p>
                    <p>{address.country}</p>
                    {address.phoneNumber && <p>📱 {address.phoneNumber}</p>}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {showForm && (
        <form onSubmit={handleSubmit} style={pageStyles.form}>
          <h3>{editingId ? 'Edit Address' : 'Add New Address'}</h3>

          <div style={pageStyles.formGroup}>
            <label>Address Type *</label>
            <select
              name="type"
              value={formData.type}
              onChange={handleInputChange}
              style={pageStyles.input}
            >
              <option value="SHIPPING">Shipping</option>
              <option value="BILLING">Billing</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div style={pageStyles.formGroup}>
            <label>Recipient Name *</label>
            <input
              type="text"
              name="recipientName"
              value={formData.recipientName}
              onChange={handleInputChange}
              placeholder="Full name"
              style={pageStyles.input}
              required
            />
          </div>

          <div style={pageStyles.formGroup}>
            <label>Phone Number</label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              placeholder="(optional)"
              style={pageStyles.input}
            />
          </div>

          <div style={pageStyles.formGroup}>
            <label>Street Address *</label>
            <input
              type="text"
              name="streetAddress"
              value={formData.streetAddress}
              onChange={handleInputChange}
              placeholder="e.g., 123 Main St"
              style={pageStyles.input}
              required
            />
          </div>

          <div style={pageStyles.formRow}>
            <div style={pageStyles.formGroup}>
              <label>City *</label>
              <input
                type="text"
                name="city"
                value={formData.city}
                onChange={handleInputChange}
                placeholder="City"
                style={pageStyles.input}
                required
              />
            </div>
            <div style={pageStyles.formGroup}>
              <label>State/Province</label>
              <input
                type="text"
                name="stateProvince"
                value={formData.stateProvince}
                onChange={handleInputChange}
                placeholder="(optional)"
                style={pageStyles.input}
              />
            </div>
          </div>

          <div style={pageStyles.formRow}>
            <div style={pageStyles.formGroup}>
              <label>Postal Code *</label>
              <input
                type="text"
                name="postalCode"
                value={formData.postalCode}
                onChange={handleInputChange}
                placeholder="ZIP/Postal"
                style={pageStyles.input}
                required
              />
            </div>
            <div style={pageStyles.formGroup}>
              <label>Country *</label>
              <input
                type="text"
                name="country"
                value={formData.country}
                onChange={handleInputChange}
                placeholder="Country"
                style={pageStyles.input}
                required
              />
            </div>
          </div>

          <div style={pageStyles.checkboxGroup}>
            <input
              type="checkbox"
              name="isDefault"
              checked={formData.isDefault}
              onChange={handleInputChange}
              id="isDefault"
            />
            <label htmlFor="isDefault" style={pageStyles.checkboxLabel}>
              Set as default address
            </label>
          </div>

          <div style={pageStyles.formButtons}>
            <button
              type="button"
              onClick={() => {
                setShowForm(false);
                setEditingId(null);
                setFormData({
                  type: 'SHIPPING',
                  recipientName: '',
                  phoneNumber: '',
                  streetAddress: '',
                  city: '',
                  stateProvince: '',
                  postalCode: '',
                  country: 'South Africa',
                  isDefault: false,
                });
              }}
              style={pageStyles.cancelButton}
            >
              Cancel
            </button>
            <button type="submit" style={pageStyles.submitButton}>
              {editingId ? 'Update Address' : 'Save Address'}
            </button>
          </div>
        </form>
      )}
    </div>
  );
}