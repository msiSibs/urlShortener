// Request/Response types for URL shortening
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

// URL info and statistics types
export interface URLInfo {
  shortCode: string;
  originalUrl: string;
  createdAt: string;
  expiresAt: string;
  clickCount: number;
  isActive: boolean;
}

export interface URLStats {
  totalUrls: number;
  totalClicks: number;
  activeUrls: number;
  expiredUrls: number;
  recentUrls: URLInfo[];
}

// Cleanup request type
export interface CleanupRequest {
  olderThanDays?: number;
  includeExpired?: boolean;
}

export interface CleanupResponse {
  deletedCount: number;
  message: string;
}

// Error handling types
export interface APIError {
  error: string;
  message: string;
  timestamp: string;
}

// Component prop types
export interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
  loading?: boolean;
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export interface InputProps {
  id?: string;
  type?: string;
  placeholder?: string;
  value?: string;
  onChange?: (value: string) => void;
  onKeyPress?: (e: React.KeyboardEvent) => void;
  disabled?: boolean;
  error?: string;
  label?: string;
  className?: string;
}

export interface CardProps {
  children: React.ReactNode;
  className?: string;
  padding?: 'sm' | 'md' | 'lg';
}

export interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}