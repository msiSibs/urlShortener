import { useState, useEffect } from 'react';
import { Card, Button, LoadingSpinner } from './';
import { urlService } from '../services/api';
import type { URLStats, URLInfo } from '../types/api';

const StatsPanel: React.FC = () => {
  const [stats, setStats] = useState<URLStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [expanded, setExpanded] = useState(false);

  const fetchStats = async () => {
    setLoading(true);
    setError('');
    
    try {
      const statsData = await urlService.getStats();
      setStats(statsData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch statistics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (expanded) {
      fetchStats();
    }
  }, [expanded]);

  const handleCleanup = async () => {
    if (!confirm('Are you sure you want to cleanup expired URLs?')) return;
    
    setLoading(true);
    try {
      await urlService.cleanupUrls({ includeExpired: true });
      await fetchStats(); // Refresh stats after cleanup
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to cleanup URLs');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (!expanded) {
    return (
      <div className="text-center">
        <Button
          variant="secondary"
          onClick={() => setExpanded(true)}
          className="mb-4"
        >
          📊 View Statistics
        </Button>
      </div>
    );
  }

  return (
    <Card className="mb-8">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
          📊 URL Statistics
        </h2>
        <div className="flex space-x-2">
          <Button size="sm" onClick={fetchStats} disabled={loading}>
            🔄 Refresh
          </Button>
          <Button 
            size="sm" 
            variant="secondary" 
            onClick={() => setExpanded(false)}
          >
            ✕ Close
          </Button>
        </div>
      </div>

      {loading ? (
        <div className="py-8">
          <LoadingSpinner size="lg" />
        </div>
      ) : error ? (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <p className="text-red-600 dark:text-red-400">{error}</p>
          <Button size="sm" onClick={fetchStats} className="mt-2">
            Try Again
          </Button>
        </div>
      ) : stats ? (
        <div className="space-y-6">
          {/* Overview Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">
                {stats.totalUrls}
              </div>
              <div className="text-sm text-blue-700 dark:text-blue-300">
                Total URLs
              </div>
            </div>
            <div className="bg-green-50 dark:bg-green-900/20 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-green-600 dark:text-green-400">
                {stats.totalClicks}
              </div>
              <div className="text-sm text-green-700 dark:text-green-300">
                Total Clicks
              </div>
            </div>
            <div className="bg-yellow-50 dark:bg-yellow-900/20 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-yellow-600 dark:text-yellow-400">
                {stats.activeUrls}
              </div>
              <div className="text-sm text-yellow-700 dark:text-yellow-300">
                Active URLs
              </div>
            </div>
            <div className="bg-red-50 dark:bg-red-900/20 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-red-600 dark:text-red-400">
                {stats.expiredUrls}
              </div>
              <div className="text-sm text-red-700 dark:text-red-300">
                Expired URLs
              </div>
            </div>
          </div>

          {/* Recent URLs */}
          {stats.recentUrls && stats.recentUrls.length > 0 && (
            <div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Recent URLs
              </h3>
              <div className="space-y-3">
                {stats.recentUrls.map((url: URLInfo, index: number) => (
                  <div
                    key={index}
                    className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600"
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center space-x-2 mb-2">
                          <code className="bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-200 px-2 py-1 rounded text-sm">
                            {url.shortCode}
                          </code>
                          <span className={`px-2 py-1 rounded text-xs font-medium ${
                            url.isActive 
                              ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-200' 
                              : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-200'
                          }`}>
                            {url.isActive ? 'Active' : 'Expired'}
                          </span>
                        </div>
                        <div className="text-sm text-gray-600 dark:text-gray-400 mb-1 break-all">
                          {url.originalUrl}
                        </div>
                        <div className="text-xs text-gray-500 dark:text-gray-500">
                          Created: {formatDate(url.createdAt)} • 
                          Expires: {formatDate(url.expiresAt)} • 
                          Clicks: {url.clickCount}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Management Actions */}
          <div className="border-t border-gray-200 dark:border-gray-600 pt-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              Management
            </h3>
            <div className="flex space-x-4">
              <Button
                variant="danger"
                size="sm"
                onClick={handleCleanup}
                disabled={loading}
              >
                🗑️ Cleanup Expired URLs
              </Button>
            </div>
          </div>
        </div>
      ) : (
        <div className="text-center py-8">
          <p className="text-gray-600 dark:text-gray-400 mb-4">
            No statistics available
          </p>
          <Button onClick={fetchStats}>Load Statistics</Button>
        </div>
      )}
    </Card>
  );
};

export default StatsPanel;