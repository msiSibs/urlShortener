import axios from 'axios';
import type { AxiosResponse } from 'axios';
import type { 
  ShortenURLRequest, 
  ShortenURLResponse, 
  URLInfo, 
  URLStats, 
  CleanupRequest, 
  CleanupResponse,
  APIError 
} from '../types/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Error handling interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.data) {
      // Backend returned an error response
      const apiError: APIError = error.response.data;
      throw new Error(apiError.message || 'An unexpected error occurred');
    }
    // Network or other error
    throw new Error(error.message || 'Network error occurred');
  }
);

export const urlService = {
  // Shorten a URL
  shortenUrl: async (request: ShortenURLRequest): Promise<ShortenURLResponse> => {
    const response: AxiosResponse<ShortenURLResponse> = await api.post('/api/shorten', request);
    return response.data;
  },

  // Get URL information by short code
  getUrlInfo: async (shortCode: string): Promise<URLInfo> => {
    const response: AxiosResponse<URLInfo> = await api.get(`/info/${shortCode}`);
    return response.data;
  },

  // Redirect to original URL (this will trigger a redirect, mainly for testing)
  redirectToUrl: async (shortCode: string): Promise<void> => {
    await api.get(`/${shortCode}`);
  },

  // Get URL statistics
  getStats: async (): Promise<URLStats> => {
    const response: AxiosResponse<URLStats> = await api.get('/api/stats');
    return response.data;
  },

  // Cleanup old URLs
  cleanupUrls: async (request: CleanupRequest = {}): Promise<CleanupResponse> => {
    const response: AxiosResponse<CleanupResponse> = await api.post('/api/cleanup', request);
    return response.data;
  },

  // Health check
  checkHealth: async (): Promise<void> => {
    await api.get('/actuator/health');
  },
};

export default api;