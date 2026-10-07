import { apiUrl } from '../api.js';
import React, { useState } from 'react';
import './UploadProduct.css';

export default function UploadProduct({ onProductAdded }) {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        stockQuantity: '',
        category: 'Clothing'
    });

    const [imageFile, setImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState('');
    const [loading, setLoading] = useState(false);
    const [statusMessage, setStatusMessage] = useState('');
    const [statusType, setStatusType] = useState('');

    const showStatus = (msg, type = 'error') => {
        setStatusMessage(msg);
        setStatusType(type);
        setTimeout(() => setStatusMessage(''), 4000);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            showStatus('Please select a valid image file');
            return;
        }

        // Validate file size (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            showStatus('Image size must be less than 5MB');
            return;
        }

        setImageFile(file);

        // Create preview
        const reader = new FileReader();
        reader.onloadend = () => {
            setImagePreview(reader.result);
        };
        reader.readAsDataURL(file);
    };

    const validateForm = () => {
        if (!formData.name.trim()) {
            showStatus('Product name is required');
            return false;
        }
        if (!formData.price || parseFloat(formData.price) <= 0) {
            showStatus('Product price must be greater than 0');
            return false;
        }
        if (!formData.stockQuantity || parseInt(formData.stockQuantity) < 0) {
            showStatus('Stock quantity must be 0 or greater');
            return false;
        }
        if (!imageFile) {
            showStatus('Product image is required');
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);

        try {
            // Create FormData for multipart upload
            const uploadFormData = new FormData();
            uploadFormData.append('file', imageFile);
            uploadFormData.append('name', formData.name);
            uploadFormData.append('description', formData.description);
            uploadFormData.append('price', formData.price);
            uploadFormData.append('stockQuantity', formData.stockQuantity);
            uploadFormData.append('category', formData.category);

            const response = await fetch(apiUrl('/product/upload'), {
                method: 'POST',
                credentials: 'include',
                body: uploadFormData
                // Don't set Content-Type header; browser will set it automatically with boundary
            });

            const data = await response.json();

            if (!response.ok) {
                showStatus(data.message || 'Failed to upload product');
                return;
            }

            showStatus(`Product "${data.name}" added successfully!`, 'success');

            // Reset form
            setFormData({
                name: '',
                description: '',
                price: '',
                stockQuantity: '',
                category: 'Clothing'
            });
            setImageFile(null);
            setImagePreview('');

            // Notify parent component
            if (onProductAdded) {
                onProductAdded(data);
            }
        } catch (error) {
            showStatus('Error uploading product. Check your connection.');
            console.error('Upload error:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="upload-container">
            <div className="upload-card">
                <h2>Upload New Product</h2>

                {statusMessage && (
                    <div className={`upload-status banner-${statusType}`}>
                        {statusMessage}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="upload-form">
                    {/* Image Upload Section */}
                    <div className="image-upload-section">
                        <label className="image-label">Product Image *</label>
                        <div className="image-upload-area">
                            {imagePreview ? (
                                <div className="image-preview">
                                    <img src={imagePreview} alt="Preview" />
                                    <button
                                        type="button"
                                        className="change-image-btn"
                                        onClick={() => document.getElementById('imageInput').click()}
                                    >
                                        Change Image
                                    </button>
                                </div>
                            ) : (
                                <div
                                    className="upload-placeholder"
                                    onClick={() => document.getElementById('imageInput').click()}
                                >
                                    <div className="upload-icon">📸</div>
                                    <p>Click to upload or drag image here</p>
                                    <small>JPG, PNG up to 5MB</small>
                                </div>
                            )}
                            <input
                                id="imageInput"
                                type="file"
                                accept="image/*"
                                onChange={handleImageChange}
                                style={{ display: 'none' }}
                                disabled={loading}
                            />
                        </div>
                    </div>

                    {/* Product Details Section */}
                    <div className="form-section">
                        <div className="form-group">
                            <label htmlFor="name">Product Name *</label>
                            <input
                                id="name"
                                type="text"
                                name="name"
                                placeholder="e.g., Classic Hoodie"
                                value={formData.name}
                                onChange={handleInputChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="description">Description</label>
                            <textarea
                                id="description"
                                name="description"
                                placeholder="e.g., Comfortable cotton blend hoodie..."
                                value={formData.description}
                                onChange={handleInputChange}
                                disabled={loading}
                                rows="3"
                            />
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="price">Price (R) *</label>
                                <input
                                    id="price"
                                    type="number"
                                    name="price"
                                    placeholder="0.00"
                                    step="0.01"
                                    min="0"
                                    value={formData.price}
                                    onChange={handleInputChange}
                                    disabled={loading}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="stockQuantity">Stock Quantity *</label>
                                <input
                                    id="stockQuantity"
                                    type="number"
                                    name="stockQuantity"
                                    placeholder="0"
                                    min="0"
                                    value={formData.stockQuantity}
                                    onChange={handleInputChange}
                                    disabled={loading}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="category">Category *</label>
                            <select
                                id="category"
                                name="category"
                                value={formData.category}
                                onChange={handleInputChange}
                                disabled={loading}
                                required
                            >
                                <option value="">Select a category</option>
                                <option value="Clothing">Clothing</option>
                                <option value="Electronics">Electronics</option>
                                <option value="Accessories">Accessories</option>
                            </select>
                        </div>
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        className="submit-btn"
                        disabled={loading}
                    >
                        {loading ? 'Uploading...' : 'Upload Product'}
                    </button>
                </form>
            </div>
        </div>
    );
}