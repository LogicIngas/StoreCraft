import React, { useState } from 'react';

export default function PaymentCheckout({
  cart,
  shippingAddress,
  onShippingAddressChange,
  onPayment,
  onClose,
  isProcessing,
  userId,
  onAddressSaved,
}) {
  const [cardToken, setCardToken] = useState('');
  const [cardLast4, setCardLast4] = useState('');
  const [cardHolderName, setCardHolderName] = useState('');
  const [saveAddress, setSaveAddress] = useState(false);
  const [addressData, setAddressData] = useState({
    recipientName: '',
    phoneNumber: '',
    streetAddress: '',
    city: '',
    stateProvince: '',
    postalCode: '',
    country: 'South Africa',
  });
  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};
    if (!shippingAddress.trim()) {
      newErrors.shippingAddress = 'Shipping address is required';
    }
    if (!cardToken.trim()) {
      newErrors.cardToken = 'Card token is required';
    }
    if (cardToken.trim().length < 3) {
      newErrors.cardToken = 'Card token must be at least 3 characters';
    }
    if (!cardHolderName.trim()) {
      newErrors.cardHolderName = 'Cardholder name is required';
    }
    if (saveAddress) {
      if (!addressData.recipientName.trim()) newErrors.recipientName = 'Recipient name is required';
      if (!addressData.streetAddress.trim()) newErrors.streetAddress = 'Street address is required';
      if (!addressData.city.trim()) newErrors.city = 'City is required';
      if (!addressData.postalCode.trim()) newErrors.postalCode = 'Postal code is required';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    onPayment(cardToken, cardLast4, cardHolderName, saveAddress, addressData);
  };

  if (!cart) return null;

  return (
    <div style={styles.modalOverlay}>
      <div style={styles.modalContent}>
        <div style={styles.modalHeader}>
          <h2>Checkout</h2>
          <button onClick={onClose} style={styles.closeButton} disabled={isProcessing}>✕</button>
        </div>

        <form onSubmit={handleSubmit} style={styles.form}>
          {/* Order Summary */}
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>Order Summary</h3>
            <div style={styles.orderSummary}>
              <div style={styles.summaryRow}>
                <span>Items ({cart.items.length}):</span>
                <span>R {cart.total}</span>
              </div>
              <div style={styles.summaryRow}>
                <span>Shipping:</span>
                <span>FREE</span>
              </div>
              <div style={{ ...styles.summaryRow, ...styles.totalRow }}>
                <strong>Total Due:</strong>
                <strong>R {cart.total}</strong>
              </div>
            </div>
          </div>

          {/* Shipping Address */}
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>Shipping Address</h3>
            <textarea
              value={shippingAddress}
              onChange={(e) => onShippingAddressChange(e.target.value)}
              placeholder="Enter your complete shipping address"
              style={styles.textarea}
              disabled={isProcessing}
            />
            {errors.shippingAddress && (
              <div style={styles.errorMessage}>{errors.shippingAddress}</div>
            )}

            <div style={styles.checkboxGroup}>
              <input
                type="checkbox"
                id="saveAddress"
                checked={saveAddress}
                onChange={(e) => setSaveAddress(e.target.checked)}
                disabled={isProcessing}
              />
              <label htmlFor="saveAddress" style={styles.checkboxLabel}>
                Save this address as my default address
              </label>
            </div>

            {saveAddress && (
              <div style={styles.addressForm}>
                <h4>Address Details</h4>
                <div style={styles.formGroup}>
                  <label>Recipient Name *</label>
                  <input
                    type="text"
                    value={addressData.recipientName}
                    onChange={(e) => setAddressData({ ...addressData, recipientName: e.target.value })}
                    placeholder="Full name"
                    style={styles.input}
                    disabled={isProcessing}
                  />
                  {errors.recipientName && <div style={styles.errorMessage}>{errors.recipientName}</div>}
                </div>
                <div style={styles.formGroup}>
                  <label>Phone Number</label>
                  <input
                    type="tel"
                    value={addressData.phoneNumber}
                    onChange={(e) => setAddressData({ ...addressData, phoneNumber: e.target.value })}
                    placeholder="(optional)"
                    style={styles.input}
                    disabled={isProcessing}
                  />
                </div>
                <div style={styles.formGroup}>
                  <label>Street Address *</label>
                  <input
                    type="text"
                    value={addressData.streetAddress}
                    onChange={(e) => setAddressData({ ...addressData, streetAddress: e.target.value })}
                    placeholder="e.g., 123 Main St"
                    style={styles.input}
                    disabled={isProcessing}
                  />
                  {errors.streetAddress && <div style={styles.errorMessage}>{errors.streetAddress}</div>}
                </div>
                <div style={styles.formRow}>
                  <div style={styles.formGroup}>
                    <label>City *</label>
                    <input
                      type="text"
                      value={addressData.city}
                      onChange={(e) => setAddressData({ ...addressData, city: e.target.value })}
                      placeholder="City"
                      style={styles.input}
                      disabled={isProcessing}
                    />
                    {errors.city && <div style={styles.errorMessage}>{errors.city}</div>}
                  </div>
                  <div style={styles.formGroup}>
                    <label>State/Province</label>
                    <input
                      type="text"
                      value={addressData.stateProvince}
                      onChange={(e) => setAddressData({ ...addressData, stateProvince: e.target.value })}
                      placeholder="(optional)"
                      style={styles.input}
                      disabled={isProcessing}
                    />
                  </div>
                </div>
                <div style={styles.formRow}>
                  <div style={styles.formGroup}>
                    <label>Postal Code *</label>
                    <input
                      type="text"
                      value={addressData.postalCode}
                      onChange={(e) => setAddressData({ ...addressData, postalCode: e.target.value })}
                      placeholder="ZIP/Postal"
                      style={styles.input}
                      disabled={isProcessing}
                    />
                    {errors.postalCode && <div style={styles.errorMessage}>{errors.postalCode}</div>}
                  </div>
                  <div style={styles.formGroup}>
                    <label>Country</label>
                    <input
                      type="text"
                      value={addressData.country}
                      onChange={(e) => setAddressData({ ...addressData, country: e.target.value })}
                      placeholder="Country"
                      style={styles.input}
                      disabled={isProcessing}
                    />
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Payment Details */}
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>Payment Details</h3>
            <div style={styles.formGroup}>
              <label>Cardholder Name *</label>
              <input
                type="text"
                value={cardHolderName}
                onChange={(e) => setCardHolderName(e.target.value)}
                placeholder="John Doe"
                style={styles.input}
                disabled={isProcessing}
              />
              {errors.cardHolderName && <div style={styles.errorMessage}>{errors.cardHolderName}</div>}
            </div>
            <div style={styles.formGroup}>
              <label>Card Token (Mock) *</label>
              <input
                type="text"
                value={cardToken}
                onChange={(e) => setCardToken(e.target.value)}
                placeholder="Enter any value (min 3 chars)"
                style={styles.input}
                disabled={isProcessing}
              />
              {errors.cardToken && <div style={styles.errorMessage}>{errors.cardToken}</div>}
              <small style={styles.helpText}>This is a mock payment gateway. Use any value with at least 3 characters.</small>
            </div>
            <div style={styles.formGroup}>
              <label>Card Last 4 Digits (Optional)</label>
              <input
                type="text"
                value={cardLast4}
                onChange={(e) => setCardLast4(e.target.value.slice(0, 4))}
                placeholder="1234"
                maxLength="4"
                style={styles.input}
                disabled={isProcessing}
              />
            </div>
          </div>

          {/* Buttons */}
          <div style={styles.buttonGroup}>
            <button type="button" onClick={onClose} style={styles.cancelButton} disabled={isProcessing}>
              Cancel
            </button>
            <button type="submit" style={styles.submitButton} disabled={isProcessing}>
              {isProcessing ? 'Processing...' : 'Complete Payment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = {
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
  modalContent: {
    background: 'white',
    borderRadius: '12px',
    maxWidth: '600px',
    width: '90%',
    maxHeight: '90vh',
    overflow: 'auto',
    boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
  },
  modalHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1.5rem',
    borderBottom: '1px solid #e2e8f0',
    background: '#f8fafc',
  },
  closeButton: {
    background: 'none',
    border: 'none',
    fontSize: '1.5rem',
    cursor: 'pointer',
    color: '#64748b',
    padding: 0,
    width: '32px',
    height: '32px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  form: {
    padding: '1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '1.5rem',
  },
  section: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
  },
  sectionTitle: {
    margin: 0,
    fontSize: '1rem',
    fontWeight: '600',
    color: '#1e293b',
  },
  orderSummary: {
    background: '#f1f5f9',
    padding: '1rem',
    borderRadius: '8px',
  },
  summaryRow: {
    display: 'flex',
    justifyContent: 'space-between',
    padding: '0.5rem 0',
    color: '#475569',
  },
  totalRow: {
    borderTop: '1px solid #cbd5e1',
    paddingTop: '1rem',
    marginTop: '0.5rem',
    color: '#0f766e',
    fontWeight: 'bold',
  },
  textarea: {
    padding: '0.75rem',
    border: '1px solid #e2e8f0',
    borderRadius: '6px',
    fontSize: '0.875rem',
    fontFamily: 'inherit',
    minHeight: '80px',
    resize: 'vertical',
  },
  checkboxGroup: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    marginTop: '0.5rem',
  },
  checkboxLabel: {
    fontSize: '0.875rem',
    color: '#1e293b',
    cursor: 'pointer',
  },
  addressForm: {
    background: '#f8fafc',
    padding: '1rem',
    borderRadius: '8px',
    border: '1px solid #e2e8f0',
  },
  formGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
    marginBottom: '0.75rem',
  },
  formRow: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1rem',
  },
  label: {
    fontSize: '0.875rem',
    fontWeight: '500',
    color: '#1e293b',
  },
  input: {
    padding: '0.75rem',
    border: '1px solid #e2e8f0',
    borderRadius: '6px',
    fontSize: '0.875rem',
    fontFamily: 'inherit',
  },
  helpText: {
    fontSize: '0.75rem',
    color: '#64748b',
    marginTop: '0.25rem',
  },
  errorMessage: {
    fontSize: '0.75rem',
    color: '#dc2626',
    marginTop: '0.25rem',
  },
  buttonGroup: {
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
    fontWeight: '500',
  },
  submitButton: {
    flex: 1,
    padding: '0.75rem',
    background: '#0f766e',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontWeight: '500',
  },
};