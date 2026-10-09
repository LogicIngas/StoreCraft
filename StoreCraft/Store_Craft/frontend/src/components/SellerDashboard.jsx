import React, { useState, useEffect } from 'react';
import { apiUrl } from '../api';
import './SellerDashboard.css';

export default function SellerDashboard({ userId }) {
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchSellerData = async () => {
            try {
                setLoading(true);
                const [productsRes, ordersRes] = await Promise.all([
                    fetch(apiUrl('/product/seller'), { credentials: 'include' }),
                    fetch(apiUrl('/order/seller'), { credentials: 'include' })
                ]);

                if (!productsRes.ok || !ordersRes.ok) {
                    throw new Error('Failed to load seller dashboard data');
                }

                const productsData = await productsRes.json();
                const ordersData = await ordersRes.json();

                setProducts(productsData);
                setOrders(ordersData);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchSellerData();
    }, []);

    if (loading) {
        return <div className="seller-dashboard-loading">Loading Dashboard...</div>;
    }

    if (error) {
        return <div className="seller-dashboard-error">Error: {error}</div>;
    }

    return (
        <div className="seller-dashboard">
            <header className="dashboard-header">
                <h1>Seller Dashboard</h1>
                <div className="dashboard-stats">
                    <div className="stat-card">
                        <h3>Total Products</h3>
                        <p>{products.length}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Total Orders</h3>
                        <p>{orders.length}</p>
                    </div>
                </div>
            </header>

            <section className="dashboard-section">
                <h2>Your Products</h2>
                {products.length === 0 ? (
                    <p className="empty-state">You haven't uploaded any products yet.</p>
                ) : (
                    <div className="products-table-container">
                        <table className="dashboard-table">
                            <thead>
                                <tr>
                                    <th>Image</th>
                                    <th>Name</th>
                                    <th>Category</th>
                                    <th>Price</th>
                                    <th>Stock</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map(product => (
                                    <tr key={product.productId}>
                                        <td>
                                            <img src={product.imageUrl} alt={product.name} className="table-img" />
                                        </td>
                                        <td>{product.name}</td>
                                        <td>{product.category}</td>
                                        <td>R {product.price.toFixed(2)}</td>
                                        <td>{product.stockQuantity}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            <section className="dashboard-section">
                <h2>Recent Orders</h2>
                {orders.length === 0 ? (
                    <p className="empty-state">No orders have been placed for your products yet.</p>
                ) : (
                    <div className="orders-table-container">
                        <table className="dashboard-table">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Total Amount</th>
                                </tr>
                            </thead>
                            <tbody>
                                {orders.map(order => (
                                    <tr key={order.orderId}>
                                        <td>#{order.orderId.substring(0, 8)}</td>
                                        <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                                        <td>
                                            <span className={`status-badge status-${order.status.toLowerCase()}`}>
                                                {order.status}
                                            </span>
                                        </td>
                                        <td>R {order.totalAmount.toFixed(2)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>
        </div>
    );
}
