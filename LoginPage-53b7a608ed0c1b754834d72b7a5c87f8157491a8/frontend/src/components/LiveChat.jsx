import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import './LiveChat.css';

export default function LiveChat({ userId, userName, userRole }) {
    const [stompClient, setStompClient] = useState(null);
    const [messages, setMessages] = useState([]);
    const [inputMessage, setInputMessage] = useState('');
    const [isConnected, setIsConnected] = useState(false);
    const [recipientId, setRecipientId] = useState('');
    const [recipientName, setRecipientName] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);
    const [conversations, setConversations] = useState([]);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [isTyping, setIsTyping] = useState(false);
    const messagesEndRef = useRef(null);
    const typingTimeoutRef = useRef(null);
    const isAdmin = userRole === 'ADMIN' || userRole === 'SELLER';

    useEffect(() => {
        if (!userId) return;

        const socket = new SockJS('http://localhost:8080/chat');
        const client = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log('✅ Connected to chat');
                setIsConnected(true);
                
                client.subscribe(`/user/${userId}/queue/messages`, (message) => {
                    const chatMessage = JSON.parse(message.body);
                    handleNewMessage(chatMessage);
                });

                client.subscribe(`/user/${userId}/queue/typing`, (data) => {
                    const typingData = JSON.parse(data.body);
                    if (typingData.senderId === recipientId) {
                        setIsTyping(typingData.isTyping);
                    }
                });

                loadConversations();
                loadUnreadCount();
            },
            onDisconnect: () => {
                console.log('❌ Disconnected from chat');
                setIsConnected(false);
            }
        });

        client.activate();
        setStompClient(client);

        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, [userId]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    useEffect(() => {
        if (recipientId) {
            loadChatHistory(recipientId);
        }
    }, [recipientId]);

    const loadConversations = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/chat/conversations/${userId}`);
            if (response.ok) {
                const data = await response.json();
                setConversations(data);
            }
        } catch (error) {
            console.error('Error loading conversations:', error);
        }
    };

    const loadUnreadCount = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/chat/unread-count/${userId}`);
            if (response.ok) {
                const count = await response.json();
                setUnreadCount(count);
            }
        } catch (error) {
            console.error('Error loading unread count:', error);
        }
    };

    const loadChatHistory = async (otherUserId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/chat/history/${userId}/${otherUserId}`);
            if (response.ok) {
                const data = await response.json();
                setMessages(data);
                
                await fetch('http://localhost:8080/api/chat/mark-read', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ userId, otherUserId })
                });
                
                loadUnreadCount();
            }
        } catch (error) {
            console.error('Error loading chat history:', error);
        }
    };

    const handleNewMessage = (message) => {
        if (message.senderId === recipientId || message.recipientId === recipientId) {
            setMessages(prev => [...prev, message]);
        }
        if (message.senderId !== userId) {
            setUnreadCount(prev => prev + 1);
            if (!isOpen) {
                showBrowserNotification(message);
            }
        }
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (!inputMessage.trim() || !recipientId || !stompClient) return;

        const message = {
            senderId: userId,
            senderName: userName,
            recipientId: recipientId,
            content: inputMessage.trim(),
            type: 'TEXT',
            timestamp: new Date().toISOString(),
            isRead: false
        };

        setMessages(prev => [...prev, { ...message, id: Date.now() }]);
        setInputMessage('');

        stompClient.publish({
            destination: '/app/chat.send',
            body: JSON.stringify(message)
        });
    };

    const handleTyping = (isTyping) => {
        if (!stompClient || !recipientId) return;
        
        if (typingTimeoutRef.current) {
            clearTimeout(typingTimeoutRef.current);
        }
        
        stompClient.publish({
            destination: '/app/chat.typing',
            body: JSON.stringify({
                senderId: userId,
                recipientId: recipientId,
                isTyping: isTyping
            })
        });
        
        if (isTyping) {
            typingTimeoutRef.current = setTimeout(() => {
                stompClient.publish({
                    destination: '/app/chat.typing',
                    body: JSON.stringify({
                        senderId: userId,
                        recipientId: recipientId,
                        isTyping: false
                    })
                });
            }, 2000);
        }
    };

    const showBrowserNotification = (message) => {
        if (!('Notification' in window)) return;
        if (Notification.permission === 'granted') {
            new Notification(`💬 ${message.senderName}`, {
                body: message.content,
                icon: '💬'
            });
        }
    };

    useEffect(() => {
        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }
    }, []);

    const getTime = (timestamp) => {
        try {
            return new Date(timestamp).toLocaleTimeString('en-ZA', { 
                hour: '2-digit', 
                minute: '2-digit' 
            });
        } catch {
            return '';
        }
    };

    const getDate = (timestamp) => {
        try {
            const date = new Date(timestamp);
            const today = new Date();
            if (date.toDateString() === today.toDateString()) {
                return 'Today';
            }
            return date.toLocaleDateString('en-ZA', { 
                month: 'short', 
                day: 'numeric' 
            });
        } catch {
            return '';
        }
    };

    const selectConversation = (otherUserId, name) => {
        setRecipientId(otherUserId);
        setRecipientName(name);
        setSelectedConversation(otherUserId);
        setMessages([]);
        setIsOpen(true);
    };

    return (
        <>
            <button 
                className="chat-bubble" 
                onClick={() => setIsOpen(!isOpen)}
            >
                💬
                {unreadCount > 0 && (
                    <span className="chat-badge">{unreadCount}</span>
                )}
            </button>

            {isOpen && (
                <div className="chat-window">
                    <div className="chat-header">
                        <div className="chat-header-info">
                            <h4>💬 Live Chat</h4>
                            <span className="chat-status">
                                {isConnected ? '🟢 Online' : '🔴 Offline'}
                            </span>
                        </div>
                        <button 
                            className="chat-close-btn"
                            onClick={() => setIsOpen(false)}
                        >
                            ✕
                        </button>
                    </div>

                    <div className="chat-body">
                        {isAdmin ? (
                            <div className="chat-admin-panel">
                                <div className="conversations-list">
                                    <h5>Conversations</h5>
                                    {conversations.length === 0 ? (
                                        <p className="no-conversations">No conversations yet</p>
                                    ) : (
                                        conversations.map(conv => (
                                            <div 
                                                key={conv.conversationId}
                                                className={`conversation-item ${selectedConversation === conv.otherUserId ? 'active' : ''}`}
                                                onClick={() => selectConversation(conv.otherUserId, `User ${conv.otherUserId.substring(0, 6)}`)}
                                            >
                                                <div className="conversation-info">
                                                    <div className="conversation-name">
                                                        User #{conv.otherUserId.substring(0, 6)}
                                                    </div>
                                                    <div className="conversation-last-message">
                                                        {conv.lastMessage}
                                                    </div>
                                                </div>
                                                {conv.unreadCount > 0 && (
                                                    <span className="conversation-badge">{conv.unreadCount}</span>
                                                )}
                                            </div>
                                        ))
                                    )}
                                </div>

                                {recipientId && (
                                    <div className="chat-messages-container">
                                        <div className="chat-messages">
                                            {messages.map((msg, index) => (
                                                <div 
                                                    key={index}
                                                    className={`message ${msg.senderId === userId ? 'sent' : 'received'}`}
                                                >
                                                    <div className="message-content">{msg.content}</div>
                                                    <div className="message-time">
                                                        {getTime(msg.createdAt || msg.timestamp)}
                                                    </div>
                                                </div>
                                            ))}
                                            <div ref={messagesEndRef} />
                                        </div>

                                        {isTyping && (
                                            <div className="typing-indicator">
                                                <span></span>
                                                <span></span>
                                                <span></span>
                                            </div>
                                        )}

                                        <form onSubmit={handleSendMessage} className="chat-input-form">
                                            <input
                                                type="text"
                                                value={inputMessage}
                                                onChange={(e) => setInputMessage(e.target.value)}
                                                onFocus={() => handleTyping(true)}
                                                onBlur={() => handleTyping(false)}
                                                placeholder={recipientId ? "Type a message..." : "Select a conversation first"}
                                                disabled={!recipientId || !isConnected}
                                                className="chat-input"
                                            />
                                            <button 
                                                type="submit" 
                                                disabled={!recipientId || !isConnected}
                                                className="chat-send-btn"
                                            >
                                                Send
                                            </button>
                                        </form>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <>
                                {recipientId ? (
                                    <>
                                        <div className="chat-messages">
                                            {messages.map((msg, index) => (
                                                <div 
                                                    key={index}
                                                    className={`message ${msg.senderId === userId ? 'sent' : 'received'}`}
                                                >
                                                    <div className="message-content">{msg.content}</div>
                                                    <div className="message-time">
                                                        {getTime(msg.createdAt || msg.timestamp)}
                                                    </div>
                                                </div>
                                            ))}
                                            <div ref={messagesEndRef} />
                                        </div>

                                        {isTyping && (
                                            <div className="typing-indicator">
                                                <span></span>
                                                <span></span>
                                                <span></span>
                                            </div>
                                        )}

                                        <form onSubmit={handleSendMessage} className="chat-input-form">
                                            <input
                                                type="text"
                                                value={inputMessage}
                                                onChange={(e) => setInputMessage(e.target.value)}
                                                onFocus={() => handleTyping(true)}
                                                onBlur={() => handleTyping(false)}
                                                placeholder="Type a message..."
                                                disabled={!isConnected}
                                                className="chat-input"
                                            />
                                            <button type="submit" disabled={!isConnected} className="chat-send-btn">
                                                Send
                                            </button>
                                        </form>
                                    </>
                                ) : (
                                    <div className="chat-start">
                                        <span>💬</span>
                                        <p>Start a conversation with support</p>
                                        <button 
                                            className="chat-start-btn"
                                            onClick={() => {
                                                setRecipientId('admin-user-id');
                                                setRecipientName('Support');
                                            }}
                                        >
                                            Chat with Support
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            )}
        </>
    );
}