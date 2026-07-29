import React, { useState, useEffect } from 'react';
import './NotificationBell.css';

export default function NotificationBell({ userId, onNotificationCountChange }) {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [isOpen, setIsOpen] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (userId) {
            fetchNotifications();
            fetchUnreadCount();
        }
    }, [userId]);

    useEffect(() => {
        if (!userId) return;

        const socket = new WebSocket('ws://localhost:8080/ws');
        
        socket.onopen = () => {
            console.log('🔌 Connected to notification WebSocket');
            socket.send(JSON.stringify({
                type: 'SUBSCRIBE',
                userId: userId,
                destination: `/user/${userId}/queue/notifications`
            }));
        };

        socket.onmessage = (event) => {
            const notification = JSON.parse(event.data);
            setNotifications(prev => [notification, ...prev]);
            setUnreadCount(prev => prev + 1);
            if (onNotificationCountChange) {
                onNotificationCountChange(unreadCount + 1);
            }
            showBrowserNotification(notification);
        };

        socket.onerror = (error) => {
            console.error('WebSocket error:', error);
        };

        return () => {
            socket.close();
        };
    }, [userId]);

    const fetchNotifications = async () => {
        try {
            setLoading(true);
            const response = await fetch(`http://localhost:8080/api/notifications/user/${userId}`);
            if (response.ok) {
                const data = await response.json();
                setNotifications(data);
            }
        } catch (error) {
            console.error('Error fetching notifications:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchUnreadCount = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/notifications/user/${userId}/unread/count`);
            if (response.ok) {
                const count = await response.json();
                setUnreadCount(count);
                if (onNotificationCountChange) {
                    onNotificationCountChange(count);
                }
            }
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            await fetch(`http://localhost:8080/api/notifications/${notificationId}/read`, {
                method: 'PUT'
            });
            setNotifications(prev => prev.map(n => 
                n.notificationId === notificationId ? { ...n, isRead: true } : n
            ));
            setUnreadCount(prev => Math.max(0, prev - 1));
            if (onNotificationCountChange) {
                onNotificationCountChange(unreadCount - 1);
            }
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    const markAllAsRead = async () => {
        try {
            await fetch(`http://localhost:8080/api/notifications/user/${userId}/read-all`, {
                method: 'PUT'
            });
            setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
            setUnreadCount(0);
            if (onNotificationCountChange) {
                onNotificationCountChange(0);
            }
        } catch (error) {
            console.error('Error marking all as read:', error);
        }
    };

    const showBrowserNotification = (notification) => {
        if (!('Notification' in window)) return;
        if (Notification.permission === 'granted') {
            new Notification(notification.title, {
                body: notification.message,
                icon: '/bell-icon.png'
            });
        }
    };

    useEffect(() => {
        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }
    }, []);

    const getIconByType = (type) => {
        switch(type) {
            case 'ORDER': return '📦';
            case 'PAYMENT': return '💳';
            case 'PROMOTION': return '🎉';
            case 'SYSTEM': return '⚙️';
            default: return '📬';
        }
    };

    const getTimeAgo = (date) => {
        const seconds = Math.floor((new Date() - new Date(date)) / 1000);
        if (seconds < 60) return `${seconds}s ago`;
        const minutes = Math.floor(seconds / 60);
        if (minutes < 60) return `${minutes}m ago`;
        const hours = Math.floor(minutes / 60);
        if (hours < 24) return `${hours}h ago`;
        const days = Math.floor(hours / 24);
        return `${days}d ago`;
    };

    return (
        <div className="notification-bell">
            <button 
                className="bell-button"
                onClick={() => setIsOpen(!isOpen)}
            >
                <span className="bell-icon">🔔</span>
                {unreadCount > 0 && (
                    <span className="badge">{unreadCount}</span>
                )}
            </button>

            {isOpen && (
                <div className="notification-dropdown">
                    <div className="dropdown-header">
                        <h4>Notifications</h4>
                        {unreadCount > 0 && (
                            <button onClick={markAllAsRead} className="mark-all-btn">
                                Mark all read
                            </button>
                        )}
                    </div>

                    <div className="notification-list">
                        {loading ? (
                            <div className="loading-notifications">Loading...</div>
                        ) : notifications.length === 0 ? (
                            <div className="empty-notifications">
                                <span>🎉</span>
                                <p>No notifications yet</p>
                            </div>
                        ) : (
                            notifications.map(notification => (
                                <div 
                                    key={notification.notificationId}
                                    className={`notification-item ${!notification.isRead ? 'unread' : ''}`}
                                    onClick={() => markAsRead(notification.notificationId)}
                                >
                                    <div className="notification-icon">
                                        {getIconByType(notification.type)}
                                    </div>
                                    <div className="notification-content">
                                        <div className="notification-title">{notification.title}</div>
                                        <div className="notification-message">{notification.message}</div>
                                        <div className="notification-time">
                                            {getTimeAgo(notification.createdAt)}
                                        </div>
                                    </div>
                                    {!notification.isRead && (
                                        <div className="unread-dot"></div>
                                    )}
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}