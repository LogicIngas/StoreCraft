import React from 'react';

/**
 * Cart Page Component
 */
export default function CartPage({ cart, onRemove, onUpdateQuantity, onCheckout }) {
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