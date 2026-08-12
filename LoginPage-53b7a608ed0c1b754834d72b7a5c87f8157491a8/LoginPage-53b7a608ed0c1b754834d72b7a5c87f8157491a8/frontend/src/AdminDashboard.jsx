import React, { useState, useEffect } from 'react';

export default function AdminDashboard({ userId }) {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [statsRes, usersRes] = await Promise.all([
          fetch('http://localhost:8080/api/admin/dashboard/stats'),
          fetch('http://localhost:8080/api/admin/users/all'),
        ]);
        if (statsRes.ok) {
          const statsData = await statsRes.json();
          setStats(statsData);
        }
        if (usersRes.ok) {
          const usersData = await usersRes.json();
          setUsers(usersData);
        }
      } catch (error) {
        console.error('Error fetching admin data:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  if (loading) return <div className="loading">Loading dashboard...</div>;

  return (
    <div className="admin-dashboard">
      <h2 className="section-title">📊 Admin Dashboard</h2>

      {/* Stats Cards */}
      <div className="stats-grid" style={{ marginBottom: '2rem' }}>
        <div className="stat-card">
          <h3>{stats?.totalUsers || 0}</h3>
          <p>Total Users</p>
        </div>
        <div className="stat-card">
          <h3>{stats?.totalProducts || 0}</h3>
          <p>Total Products</p>
        </div>
        <div className="stat-card">
          <h3>{stats?.availableProducts || 0}</h3>
          <p>Available Products</p>
        </div>
        <div className="stat-card">
          <h3>{stats?.totalOrders || 0}</h3>
          <p>Total Orders</p>
        </div>
        <div className="stat-card">
          <h3>R {stats?.totalRevenue?.toFixed(2) || '0.00'}</h3>
          <p>Total Revenue</p>
        </div>
        <div className="stat-card">
          <h3>{stats?.pendingOrders || 0}</h3>
          <p>Pending Orders</p>
        </div>
      </div>

      {/* Users List */}
      <div className="orders-container" style={{ marginBottom: '2rem' }}>
        <h3>Registered Users</h3>
        {users.length === 0 ? (
          <p>No users found.</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user, idx) => (
                <tr key={user.userId || idx} style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td>{user.firstName} {user.lastName}</td>
                  <td>{user.email}</td>
                  <td>{user.role?.name || 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Recent Orders */}
      <div className="orders-container">
        <h3>Recent Orders</h3>
        {stats?.recentOrders && stats.recentOrders.length > 0 ? (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th>Order ID</th>
                <th>User ID</th>
                <th>Total</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {stats.recentOrders.map((order, idx) => (
                <tr key={order.orderId || idx} style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td>{order.orderId?.substring(0, 8) || 'N/A'}</td>
                  <td>{order.userId?.substring(0, 8) || 'N/A'}</td>
                  <td>R {order.totalAmount}</td>
                  <td>{order.status}</td>
                  <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No recent orders.</p>
        )}
      </div>
    </div>
  );
}