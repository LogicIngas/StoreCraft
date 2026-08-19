import React, { useState, useEffect } from 'react';
import {
  Box,
  Tab,
  Tabs,
  Card,
  CardContent,
  Typography,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Select,
  MenuItem,
  CircularProgress,
  Container
} from '@mui/material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';

export default function AdminDashboard({ userId }) {
  const [tabIndex, setTabIndex] = useState(0);
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [revenueData, setRevenueData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [statsRes, usersRes, ordersRes, revRes] = await Promise.all([
        fetch('http://localhost:8080/api/admin/dashboard/stats'),
        fetch('http://localhost:8080/api/admin/users/all'),
        fetch('http://localhost:8080/api/admin/orders/all'),
        fetch('http://localhost:8080/api/admin/revenue/monthly')
      ]);

      if (statsRes.ok) setStats(await statsRes.json());
      if (usersRes.ok) setUsers(await usersRes.json());
      if (ordersRes.ok) {
        const ordersData = await ordersRes.json();
        // Sort orders by most recent
        ordersData.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        setOrders(ordersData);
      }
      if (revRes.ok) {
        const revData = await revRes.json();
        // Convert map to array for recharts
        const chartData = Object.keys(revData).map(key => ({
          name: key,
          Revenue: revData[key]
        }));
        
        // Reverse so chronological order is likely better (assuming keys are months)
        setRevenueData(chartData.reverse());
      }
    } catch (error) {
      console.error('Error fetching admin data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabIndex(newValue);
  };

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      const res = await fetch(`http://localhost:8080/api/admin/orders/${orderId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
      });
      if (res.ok) {
        // Update local state
        setOrders(orders.map(o => o.orderId === orderId ? { ...o, status: newStatus } : o));
      }
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Admin Dashboard
      </Typography>

      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={tabIndex} onChange={handleTabChange} aria-label="admin tabs">
          <Tab label="Overview" />
          <Tab label="Users" />
          <Tab label="Orders" />
        </Tabs>
      </Box>

      {/* Overview Tab */}
      {tabIndex === 0 && (
        <Box>
          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} sm={4} md={4}>
              <Card>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>Total Users</Typography>
                  <Typography variant="h4">{stats?.totalUsers || 0}</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <Card>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>Total Orders</Typography>
                  <Typography variant="h4">{stats?.totalOrders || 0}</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <Card>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>Total Revenue</Typography>
                  <Typography variant="h4">R {stats?.totalRevenue?.toFixed(2) || '0.00'}</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <Card>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>Pending Orders</Typography>
                  <Typography variant="h4">{stats?.pendingOrders || 0}</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <Card>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>Total Products</Typography>
                  <Typography variant="h4">{stats?.totalProducts || 0}</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <Card>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>Available Products</Typography>
                  <Typography variant="h4">{stats?.availableProducts || 0}</Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          <Typography variant="h6" gutterBottom>Monthly Revenue</Typography>
          <Paper sx={{ p: 2, height: 400 }}>
            {revenueData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={revenueData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="Revenue" fill="#1976d2" />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <Typography>No revenue data available.</Typography>
            )}
          </Paper>
        </Box>
      )}

      {/* Users Tab */}
      {tabIndex === 1 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>User ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Role</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {users.map((user) => (
                <TableRow key={user.userId}>
                  <TableCell>{user.userId?.substring(0, 8)}...</TableCell>
                  <TableCell>{user.firstName} {user.lastName}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.role?.name || 'USER'}</TableCell>
                </TableRow>
              ))}
              {users.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} align="center">No users found.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Orders Tab */}
      {tabIndex === 2 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Order ID</TableCell>
                <TableCell>User ID</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Total Amount</TableCell>
                <TableCell>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {orders.map((order) => (
                <TableRow key={order.orderId}>
                  <TableCell>{order.orderId?.substring(0, 8)}...</TableCell>
                  <TableCell>{order.userId?.substring(0, 8)}...</TableCell>
                  <TableCell>{new Date(order.createdAt).toLocaleDateString()}</TableCell>
                  <TableCell>R {order.totalAmount?.toFixed(2)}</TableCell>
                  <TableCell>
                    <Select
                      value={order.status || 'PENDING'}
                      onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                      size="small"
                      sx={{ minWidth: 120 }}
                    >
                      <MenuItem value="PENDING">PENDING</MenuItem>
                      <MenuItem value="CONFIRMED">CONFIRMED</MenuItem>
                      <MenuItem value="SHIPPED">SHIPPED</MenuItem>
                      <MenuItem value="DELIVERED">DELIVERED</MenuItem>
                      <MenuItem value="CANCELLED">CANCELLED</MenuItem>
                    </Select>
                  </TableCell>
                </TableRow>
              ))}
              {orders.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} align="center">No orders found.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
}