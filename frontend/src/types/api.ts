export interface ShortenURLRequest {
  url: string;
  expiresInDays?: number;
}

export interface ShortenURLResponse {
  shortUrl: string;
  shortCode: string;
  originalUrl: string;
  expiresAt: string;
}

export interface APIError {
  error: string;
  message: string;
  timestamp: string;
}