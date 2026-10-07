/**
 * Central API configuration.
 *
 * The backend origin is injected at build time via VITE_API_URL. When the
 * variable is absent the app falls back to same-origin relative URLs, which is
 * what happens in production because nginx serves the SPA and proxies the API
 * from a single host. That keeps the browser on one origin, so CORS never
 * comes into play and the deployment works on any Render hostname.
 */
const configuredBase = import.meta.env.VITE_API_URL || '';

export const API_BASE = configuredBase.replace(/\/+$/, '');

/**
 * Builds an absolute-or-relative request URL from a backend path.
 * Already-absolute URLs are passed through untouched.
 */
export function apiUrl(path) {
  if (!path) return API_BASE || '/';
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_BASE}${path.startsWith('/') ? path : `/${path}`}`;
}

export default apiUrl;
