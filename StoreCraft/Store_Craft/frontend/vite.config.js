import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, import.meta.dirname, '')

  // Backend used by `npm run dev` when running the Spring Boot app on 8080.
  // In production the SPA is served by nginx alongside the API, so requests
  // stay same-origin and this proxy is not involved.
  const devApiTarget = env.VITE_DEV_API_TARGET || 'http://localhost:8080'

  // Paths served by the Spring Boot backend. Everything not listed here is
  // treated as a static asset belonging to the SPA.
  const backendPaths = [
    '/user',
    '/product',
    '/cart',
    '/order',
    '/payment',
    '/address',
    '/coupon',
    '/review',
    '/admin',
    '/api',
    '/chat',
    '/ws',
    '/images',
    '/uploads',
  ]

  const proxy = Object.fromEntries(
    backendPaths.map((route) => [
      route,
      {
        target: devApiTarget,
        changeOrigin: true,
        // Required for the SockJS/Stomp endpoints on /ws and /chat.
        ws: true,
      },
    ])
  )

  return {
    plugins: [react()],
    server: {
      host: true,
      port: 5173,
      proxy,
    },
  }
})
