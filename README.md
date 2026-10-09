<div align="center">

<img src="https://img.shields.io/badge/StoreCraft-E--Marketplace-6c63ff?style=for-the-badge&logo=shopify&logoColor=white" alt="StoreCraft"/>

# 🛍️ StoreCraft

### A full-stack African E-Marketplace — connecting buyers and sellers with a seamless modern shopping experience.

[![Live Demo](https://img.shields.io/badge/🚀%20Live%20Demo-storecraft--fe.onrender.com-6c63ff?style=for-the-badge)](https://storecraft-fe.onrender.com)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.2-6db33f?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-React%20%2B%20Vite-61dafb?style=for-the-badge&logo=react&logoColor=black)](https://react.dev)
[![Database](https://img.shields.io/badge/Database-Supabase%20PostgreSQL-3ecf8e?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com)
[![Deployed on](https://img.shields.io/badge/Deployed%20on-Render-46e3b7?style=for-the-badge&logo=render&logoColor=white)](https://render.com)

</div>

---

## 📌 Table of Contents

- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [⚡ Quick Start — Run Locally](#-quick-start--run-locally)
  - [Prerequisites](#prerequisites)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Backend Setup](#2-backend-setup)
  - [3. Frontend Setup](#3-frontend-setup)
- [🔐 Environment Variables](#-environment-variables)
  - [Backend `.env`](#backend-env)
  - [Frontend `.env`](#frontend-env)
- [🌍 Live Deployment Testing Guide](#-live-deployment-testing-guide)
- [📸 Screenshots](#-screenshots)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

---

## ✨ Features

| Category | Feature |
|---|---|
| 🔐 **Authentication** | Email/password registration & login with BCrypt hashing |
| 🌐 **Social Login** | One-click Google Sign-In via Supabase OAuth |
| 📧 **Email Verification** | Secure 24-hour token emailed on registration; checkout blocked until verified |
| 🔑 **Password Reset** | Forgot-password flow with 1-hour expiry reset link via email |
| 🛒 **Shopping Cart** | Add, remove, update quantities — persisted per user |
| ❤️ **Wishlist** | Save products and move to cart with one click |
| 💳 **Secure Checkout** | Luhn-algorithm card validation; email receipt sent on successful payment |
| 📦 **Order Management** | Full order history with real-time status tracking |
| 📬 **Email Notifications** | Transactional emails via **Brevo** (order confirmed, status changed) |
| 👤 **Profile Management** | Update name, email, password, and profile picture |
| 📍 **Address Manager** | Save and manage multiple shipping addresses |
| 🏪 **Seller Dashboard** | Upload products, manage listings, view your sales |
| 🛡️ **Admin Dashboard** | Manage all users, orders, products, and change order statuses |
| 🍪 **Cookie Consent** | POPIA/GDPR-compliant cookie consent banner |
| 📱 **Responsive Design** | Mobile-first UI with dark-mode glassmorphism aesthetics |
| 🔗 **Rich Link Previews** | Open Graph meta tags for beautiful WhatsApp/social share cards |

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        CLIENT BROWSER                        │
│              React 18 + Vite  (Render Static Site)           │
└───────────────────────────┬──────────────────────────────────┘
                            │ REST API (JSON)
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                     SPRING BOOT 3.2                          │
│              (Java 17 — Render Web Service)                  │
│                                                              │
│  Controllers → Services → Repositories → JPA Entities       │
│                                                              │
│  ├── UserController      (auth, verification, reset)         │
│  ├── ProductController   (CRUD, Cloudinary image upload)     │
│  ├── CartController      (cart management)                   │
│  ├── OrderController     (order lifecycle)                   │
│  ├── PaymentController   (checkout, email receipt)           │
│  ├── WishlistController  (wishlist CRUD)                     │
│  └── AddressController   (address management)                │
└───────────┬──────────────────────────┬───────────────────────┘
            │                          │
            ▼                          ▼
┌─────────────────────┐   ┌────────────────────────────────────┐
│  Supabase PostgreSQL│   │  External Services                 │
│  (Transaction Pool) │   │  ├── Brevo  (transactional email)  │
│                     │   │  ├── Cloudinary (image storage)    │
│  Tables:            │   │  └── Supabase (Google OAuth/JWKS)  │
│  users, roles,      │   └────────────────────────────────────┘
│  products, cart,    │
│  cart_items,        │
│  orders, order_items│
│  payments, wishlist │
│  addresses          │
└─────────────────────┘
```

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| **Java** | 17 | Primary language |
| **Spring Boot** | 3.2.0 | Application framework |
| **Spring Security** | 6.x | Auth & CORS |
| **Spring Data JPA** | 3.x | ORM / database layer |
| **PostgreSQL** | 15 | Relational database (via Supabase) |
| **Nimbus JOSE JWT** | 9.x | Supabase JWT verification (RS256) |
| **Thymeleaf** | 3.x | HTML email templates |
| **Brevo HTTP API** | v3 | Transactional email dispatch |
| **Cloudinary** | 1.x | Product image storage & CDN |
| **Maven** | 3.9 | Build tool |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| **React** | 18 | UI framework |
| **Vite** | 5.x | Build tool & dev server |
| **Vanilla CSS** | — | All styling (no Tailwind) |
| **Supabase JS** | 2.x | Google OAuth client |

### Infrastructure
| Service | Purpose |
|---|---|
| **Render** | Backend (Web Service) + Frontend (Static Site) hosting |
| **Supabase** | PostgreSQL database + Google Auth provider |
| **Brevo** | Email delivery (SMTP fallback + HTTP API primary) |
| **Cloudinary** | Product image uploads & CDN |
| **GitHub** | Source control & CI trigger for Render |

---

## ⚡ Quick Start — Run Locally

### Prerequisites

Make sure you have these installed before you begin:

- ✅ **Java 17+** — [Download](https://adoptium.net/)
- ✅ **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- ✅ **Node.js 18+** — [Download](https://nodejs.org/)
- ✅ **Git** — [Download](https://git-scm.com/)
- ✅ A **Supabase** project (free) — [supabase.com](https://supabase.com)
- ✅ A **Brevo** account (free) — [brevo.com](https://brevo.com)
- ✅ A **Cloudinary** account (free) — [cloudinary.com](https://cloudinary.com)

---

### 1. Clone the Repository

```bash
git clone https://github.com/LogicIngas/StoreCraft.git
cd StoreCraft/StoreCraft/Store_Craft
```

---

### 2. Backend Setup

#### a) Create the backend environment file

Create a file called `.env` in `StoreCraft/Store_Craft/`:

```env
# Supabase PostgreSQL (Transaction Pooler)
DB_URL=jdbc:postgresql://<your-supabase-host>:6543/postgres?sslmode=require&prepareThreshold=0
DB_USERNAME=postgres.<your-project-ref>
DB_PASSWORD=<your-db-password>

# Spring
SPRING_PROFILES_ACTIVE=dev

# CORS — allow your local frontend
FRONTEND_URL=http://localhost:5173

# Brevo Email
BREVO_API_KEY=<your-brevo-api-key>
BREVO_SMTP_ENABLED=false
MAIL_FROM=<your-verified-sender@email.com>
MAIL_FROM_NAME=StoreCraft

# Cloudinary
CLOUDINARY_URL=cloudinary://<api_key>:<api_secret>@<cloud_name>

# Supabase JWT (for Google login verification)
SUPABASE_URL=https://<your-project-ref>.supabase.co
SUPABASE_JWT_SECRET=<your-supabase-jwt-secret>

# App base URL (used in email links)
APP_BASE_URL=http://localhost:5173
```

#### b) Run the backend

```bash
# On Windows
.\mvnw spring-boot:run

# On macOS / Linux
./mvnw spring-boot:run
```

The backend will start on **http://localhost:8080**

> 💡 On first run, Spring will auto-create all database tables via `ddl-auto=update`.

---

### 3. Frontend Setup

#### a) Navigate to the frontend directory

```bash
cd frontend
```

#### b) Create the frontend environment file

Create `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_SUPABASE_URL=https://<your-project-ref>.supabase.co
VITE_SUPABASE_ANON_KEY=<your-supabase-anon-key>
```

#### c) Install dependencies and start

```bash
npm install
npm run dev
```

The frontend will start on **http://localhost:5173** 🎉

> ✅ You're all set! Open `http://localhost:5173` in your browser.

---

## 🔐 Environment Variables

### Backend `.env`

| Variable | Required | Description |
|---|---|---|
| `DB_URL` | ✅ | Supabase PostgreSQL JDBC connection string |
| `DB_USERNAME` | ✅ | Supabase DB username |
| `DB_PASSWORD` | ✅ | Supabase DB password |
| `SPRING_PROFILES_ACTIVE` | ✅ | `dev` for local, `prod` for Render |
| `FRONTEND_URL` | ✅ | Comma-separated allowed origins for CORS |
| `BREVO_API_KEY` | ✅ | Brevo HTTP API key for sending emails |
| `MAIL_FROM` | ✅ | Verified sender email on Brevo |
| `CLOUDINARY_URL` | ✅ | Full Cloudinary connection string |
| `SUPABASE_URL` | ✅ | Your Supabase project URL |
| `SUPABASE_JWT_SECRET` | ✅ | Used to verify Google OAuth tokens |
| `APP_BASE_URL` | ✅ | Root URL used inside reset/verify email links |

### Frontend `.env`

| Variable | Required | Description |
|---|---|---|
| `VITE_API_URL` | ✅ | Backend base URL |
| `VITE_SUPABASE_URL` | ✅ | Supabase project URL |
| `VITE_SUPABASE_ANON_KEY` | ✅ | Supabase public anon key |

> ⚠️ **Never commit `.env` files to Git.** They are listed in `.gitignore`.

---

## 🌍 Live Deployment Testing Guide

The application is fully deployed and live. You can test every feature without any local setup.

### 🔗 Live URL: **[https://storecraft-fe.onrender.com](https://storecraft-fe.onrender.com)**

> ℹ️ **Note:** The backend runs on Render's free tier. The first request after inactivity may take **30–60 seconds** to warm up. This is normal — just wait and refresh!

---

### Test Checklist

#### 🔐 1. Register & Email Verification
1. Click **Get Started** or the Login button on the landing page
2. Switch to the **Register** tab
3. Fill in your name, a **real email address you can check**, a password, and choose **Buyer**
4. Click **Create Account**
5. Open your email inbox — you should receive a **"Verify Your Email"** message
6. Click the link → you'll be taken back to the site with a ✅ green success banner

#### 🛒 2. Checkout Block (Unverified)
1. Add any product to your cart before verifying your email
2. Click **Checkout** → you should see a red error: *"Please check your email and verify your account before checking out"*

#### 💳 3. Checkout & Payment Receipt
1. After verifying, add a product to your cart and go to checkout
2. Fill in your shipping address
3. For **Card Number**, use the test card: `4242 4242 4242 4242`
   - Expiry: any future date (e.g. `12/26`)
   - CVV: any 3 digits (e.g. `123`)
4. Click **Pay Now**
5. Check your email — you should receive an **Order Confirmation receipt** 📧

#### 🔑 4. Forgot / Reset Password
1. Log out and click **Login**
2. Click **"Forgot password?"**
3. Enter your email and click **Send Reset Link**
4. Open your inbox and click the link
5. The site opens the **Set New Password** form directly
6. Enter a new password and click **Update Password**
7. Log in with the new password to confirm it works ✅

#### 🌐 5. Google Login
1. Log out and click **Continue with Google**
2. Complete Google sign-in
3. You'll be logged in automatically — no email verification required (Google pre-verifies)
4. Add to cart and proceed to checkout normally

#### 🏪 6. Seller Flow
1. Register a new account and choose the **Seller** role
2. Look for the **📊 Dashboard** and **Upload Product** options in the navbar
3. Upload a product with a name, price, category, and image
4. Log out and log back in as a Buyer — your product should appear in the store

#### 🛡️ 7. Admin Flow
1. Contact the repository owner to have your account elevated to **ADMIN**
2. Log in — you'll be redirected to the **Admin Dashboard** automatically
3. You can manage users, change order statuses, and oversee all products
4. When you change an order status, the buyer receives an **email notification**

#### 🔗 8. Rich Link Preview
1. Copy the live URL: `https://storecraft-fe.onrender.com`
2. Paste it into **WhatsApp**, iMessage, or any social platform
3. A rich preview card with the StoreCraft logo and description should appear

---

## 📸 Screenshots

<img width="1907" height="995" alt="image" src="https://github.com/user-attachments/assets/a065b26f-660b-4fb7-802d-dcfcc40fce60" />


---

## 🤝 Contributing

Contributions, bug reports, and feature requests are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feat/your-feature`
3. Commit your changes: `git commit -m "feat: add your feature"`
4. Push to your branch: `git push origin feat/your-feature`
5. Open a **Pull Request**

Please keep commit messages short and descriptive.

---

## 📄 License

This project was built as an academic/portfolio project.  
All rights reserved © 2026 StoreCraft / LogicIngas.

---

<div align="center">

Made with ❤️ by the StoreCraft team · Powered by Spring Boot, React & Supabase

[![Live Demo](https://img.shields.io/badge/🚀%20Try%20It%20Live-storecraft--fe.onrender.com-6c63ff?style=for-the-badge)](https://storecraft-fe.onrender.com)

</div>
