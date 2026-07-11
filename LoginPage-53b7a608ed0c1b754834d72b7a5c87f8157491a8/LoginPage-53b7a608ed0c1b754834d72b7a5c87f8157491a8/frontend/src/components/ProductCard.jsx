import React from 'react';

export default function ProductCard({ product, onAddToCart }) {
    return (
        <div style={styles.card}>
            <div style={styles.imageContainer}>
                <img
                    src={product.imageUrl}
                    alt={product.name}
                    style={styles.image}
                    // Safely handles missing images or typographical mistakes
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = 'https://placehold.co/300x350?text=No+Image+Found';
                    }}
                />
            </div>
            <div style={styles.info}>
                <span style={styles.category}>{product.category}</span>
                <h3 style={styles.title}>{product.name}</h3>
                <p style={styles.description}>{product.description}</p>
                <div style={styles.footerRow}>
                    <span style={styles.price}>R {product.price}</span>
                    <button
                        onClick={() => onAddToCart(product.productId)}
                        style={styles.button}
                    >
                        Add To Cart
                    </button>
                </div>
            </div>
        </div>
    );
}

// Quick modern styles for testing your layout out-of-the-box
const styles = {
    card: {
        background: '#ffffff',
        borderRadius: '12px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
        transition: 'transform 0.2s',
        border: '1px solid #e2e8f0',
    },
    imageContainer: {
        width: '100%',
        height: '240px',
        backgroundColor: '#f8fafc',
    },
    image: {
        width: '100%',
        height: '100%',
        objectFit: 'cover',
    },
    info: {
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        flexGrow: 1,
    },
    category: {
        fontSize: '11px',
        textTransform: 'uppercase',
        color: '#64748b',
        fontWeight: '700',
        marginBottom: '4px',
    },
    title: {
        fontSize: '18px',
        margin: '0 0 8px 0',
        color: '#1e293b',
    },
    description: {
        fontSize: '13px',
        color: '#64748b',
        margin: '0 0 16px 0',
        lineHeight: '1.4',
    },
    footerRow: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: 'auto',
    },
    price: {
        fontSize: '18px',
        fontWeight: '700',
        color: '#2563eb',
    },
    button: {
        backgroundColor: '#2563eb',
        color: '#fff',
        border: 'none',
        padding: '8px 16px',
        borderRadius: '6px',
        fontWeight: '600',
        cursor: 'pointer',
    }
};