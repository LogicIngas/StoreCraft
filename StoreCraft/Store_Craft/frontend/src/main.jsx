import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// Intercept fetch to add the JWT token to all requests
const originalFetch = window.fetch;
window.fetch = async (...args) => {
  let [resource, config] = args;
  
  const currentUser = localStorage.getItem('currentUser');
  if (currentUser) {
    try {
      const user = JSON.parse(currentUser);
      if (user && user.token) {
        config = config || {};
        const headers = new Headers(config.headers || {});
        headers.set('Authorization', `Inga ${user.token}`);
        config.headers = headers;
      }
    } catch (e) {
      console.error('Error intercepting fetch', e);
    }
  }
  
  return originalFetch(resource, config);
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
