import axios from 'axios';
import type { AxiosResponse } from 'axios';
import type { ShortenURLRequest, ShortenURLResponse } from '../types/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const urlService = {
  shortenUrl: async (request: ShortenURLRequest): Promise<ShortenURLResponse> => {
    const response: AxiosResponse<ShortenURLResponse> = await api.post('/api/shorten', request);
    return response.data;
  },

  checkHealth: async (): Promise<void> => {
    await api.get('/actuator/health');
  },
};

export default api;