import React from 'react';

export default function StatusBanner({ message, type }) {
    if (!message) return null;

    return (
        <div className={`status-banner ${type}`}>
            {message}
        </div>
    );
}