import React, { useState, useEffect } from 'react';

/**
 * Orders Page Component
 */
export default function OrdersPage({ userId }) {
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