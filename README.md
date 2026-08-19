# 🌍 AfriConnect

<p align="center">
  <img src="https://img.shields.io/badge/Java-22-007396?style=for-the-badge&logo=java&logoColor=white" alt="Java 22"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3.2.0"/>
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=white" alt="React 19"/>
  <img src="https://img.shields.io/badge/Vite-6-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite 6"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL 8.0"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
</p>

AfriConnect is a full‑stack e‑commerce web application that connects buyers and sellers of authentic African products.  
It features user authentication, product browsing, shopping cart, wishlist, order management, address management, and an admin dashboard.

---

## 🎯 UML Class Diagram

<p align="center">
  <img width="1505" height="1492" alt="UMLFinal" src="https://github.com/user-attachments/assets/68d09471-691d-4da9-bac2-a424674a36cc" />
</p>

---

## 📚 Table of Contents

### 🚀 Getting Started
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Project Structure](#-project-structure)

### ⚙️ Setup & Installation
- [Prerequisites](#-prerequisites)
- [Clone the Repository](#-clone-the-repository)
- [Backend Setup (Spring Boot)](#️-backend-setup-spring-boot)
- [Frontend Setup (React + Vite)](#-frontend-setup-react--vite)
### 🏃 Running the App
- [Running the Application](#-running-the-application)
- [Default Admin Credentials](#-default-admin-credentials)

### 📖 Documentation
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)

### 🤝 Contributing
- [Contributing](#-contributing)
- [License](#-license)

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 22 | Programming language |
| Spring Boot | 3.2.0 | Framework |
| Spring Data JPA | – | ORM and database access |
| Spring Web | – | REST APIs |
| Spring WebSocket | – | Real‑time notifications |
| Spring Mail | – | Email services |
| Thymeleaf | – | Email templates |
| MySQL | 8.0 | Relational database |
| Maven | – | Build tool |
| Docker | – | Containerization (optional) |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 19 | UI library |
| Vite | 6 | Build tool and development server |
| Axios | – | HTTP client |
| CSS3 | – | Custom styling with responsive design |

### DevOps & Tools
- **Git** – Version control
- **GitHub** – Repository hosting
- **Docker Compose** – Multi‑container orchestration

---

## ✨ Features

### 👤 Authentication & Authorization
- ✅ User registration with role selection (**BUYER**, **SELLER**, **ADMIN**)
- ✅ Secure login with email and password
- ✅ Role‑based access control (RBAC)

### 🛍️ Storefront
- ✅ Browse all products with search and category filters
- ✅ View product details with images, pricing, and stock status
- ✅ Responsive grid layout

### 🛒 Shopping Cart
- ✅ Add/remove items
- ✅ Update quantities
- ✅ View cart total
- ✅ Proceed to checkout

### ❤️ Wishlist
- ✅ Save favorite products
- ✅ Bulk add to cart
- ✅ Share wishlist

### 📦 Orders
- ✅ View order history with status tracking
- ✅ Order statuses: `PENDING` → `PAID` → `SHIPPED` → `DELIVERED` → `CANCELLED`

### 📍 Address Management
- ✅ Add, edit, delete addresses
- ✅ Set default address
- ✅ Address auto‑fill during checkout

### 💳 Payment (Mock)
- ✅ Simulated payment gateway
- ✅ Card token validation
- ✅ Order confirmation emails

### 📊 Admin Dashboard
- ✅ View total users, products, orders, revenue
- ✅ Manage user list
- ✅ View recent orders

---

## 📁 Project Structure
