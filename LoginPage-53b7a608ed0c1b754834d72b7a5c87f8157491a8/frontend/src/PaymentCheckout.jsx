import React, { useState } from 'react';

export default function PaymentCheckout({
                                            cart,
                                            shippingAddress,
                                            onShippingAddressChange,
                                            onPayment,
                                            onClose,
                                            isProcessing
                                        }) {
    const [cardToken, setCardToken] = useState('');
    const [cardLast4, setCardLast4] = useState('');
    const [cardHolderName, setCardHolderName] = useState('');
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

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        onPayment(cardToken, cardLast4, cardHolderName);
    };

    if (!cart) return null;

    return (
        <div style={styles.modalOverlay}>
            <div style={styles.modalContent}>
                <div style={styles.modalHeader}>
                    <h2>Checkout</h2>
                    <button
                        onClick={onClose}
                        style={styles.closeButton}
                        disabled={isProcessing}
                    >
                        ✕
                    </button>
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
                            <div style={{...styles.summaryRow, ...styles.totalRow}}>
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
                    </div>

                    {/* Payment Details */}
                    <div style={styles.section}>
                        <h3 style={styles.sectionTitle}>Payment Details</h3>

                        <div style={styles.formGroup}>
                            <label style={styles.label}>
                                Cardholder Name *
                            </label>
                            <input
                                type="text"
                                value={cardHolderName}
                                onChange={(e) => setCardHolderName(e.target.value)}
                                placeholder="John Doe"
                                style={styles.input}
                                disabled={isProcessing}
                            />
                            {errors.cardHolderName && (
                                <div style={styles.errorMessage}>{errors.cardHolderName}</div>
                            )}
                        </div>

                        <div style={styles.formGroup}>
                            <label style={styles.label}>
                                Card Token (Mock) *
                            </label>
                            <input
                                type="text"
                                value={cardToken}
                                onChange={(e) => setCardToken(e.target.value)}
                                placeholder="Enter any value (min 3 chars) - mock payment"
                                style={styles.input}
                                disabled={isProcessing}
                            />
                            {errors.cardToken && (
                                <div style={styles.errorMessage}>{errors.cardToken}</div>
                            )}
                            <small style={styles.helpText}>
                                This is a mock payment gateway. Use any value with at least 3 characters.
                            </small>
                        </div>

                        <div style={styles.formGroup}>
                            <label style={styles.label}>
                                Card Last 4 Digits (Optional)
                            </label>
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
                        <button
                            type="button"
                            onClick={onClose}
                            style={styles.cancelButton}
                            disabled={isProcessing}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            style={styles.submitButton}
                            disabled={isProcessing}
                        >
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
        maxWidth: '500px',
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
    formGroup: {
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
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