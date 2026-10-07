import { apiUrl } from '../api.js';
import React, { useState, useEffect } from 'react';

export default function OrdersPage({ userId }) {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadOrders = async () => {
            try {
                const response = await fetch(apiUrl(`/order/user/${userId}`), {
                    credentials: 'include',
                });
                if (response.ok) {
                    const data = await response.json();
                    // data is now a List<OrderDTO> directly
                    setOrders(data || []);
                } else {
                    console.error('Failed to load orders:', response.status);
                    setOrders([]);
                }
            } catch (error) {
                console.error('Error loading orders:', error);
                setOrders([]);
            } finally {
                setLoading(false);
            }
        };

        loadOrders();
    }, [userId]);

    if (loading) return <div className="loading">Loading orders...</div>;

    if (!orders || orders.length === 0) {
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
                            <h3>Order #{order.orderId?.substring(0, 8) || 'N/A'}</h3>
                            <p className="order-date">
                                {order.createdAt ? new Date(order.createdAt).toLocaleDateString('en-ZA', {
                                    year: 'numeric',
                                    month: 'long',
                                    day: 'numeric',
                                    hour: '2-digit',
                                    minute: '2-digit'
                                }) : 'Date not available'}
                            </p>
                        </div>
                        <span className={`order-status ${order.status?.toLowerCase() || ''}`}>
                            {order.status || 'Unknown'}
                        </span>
                    </div>
                    <div className="order-items">
                        {order.items && order.items.length > 0 ? (
                            order.items.map(item => (
                                <div key={item.orderItemId || Math.random()} className="order-item">
                                    <span>{item.productName || 'Product'}</span>
                                    <span>x{item.quantity || 0}</span>
                                    <span>R {(item.subtotal ?? 0).toFixed(2)}</span>
                                </div>
                            ))
                        ) : (
                            <p className="no-items">No items in this order</p>
                        )}
                    </div>
                    {order.shippingAddress && (
                        <div className="order-shipping">
                            <p><strong>📍 Shipping Address:</strong> {order.shippingAddress}</p>
                        </div>
                    )}
                    <div className="order-total">
                        <strong>Total: R {(order.totalAmount ?? 0).toFixed(2)}</strong>
                    </div>
                </div>
            ))}
        </div>
    );
}