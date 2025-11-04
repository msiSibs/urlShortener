import { useState } from 'react';
import { Button, Input, Card, URLResult, StatsPanel, SecurityNotice } from './components';
import { urlService } from './services/api';
import type { ShortenURLResponse } from './types/api';
import './App.css';

function App() {
  const [url, setUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<ShortenURLResponse | null>(null);
  const [error, setError] = useState('');

  const isValidUrl = (urlString: string): boolean => {
    try {
      const url = new URL(urlString);
      return url.protocol === 'http:' || url.protocol === 'https:';
    } catch {
      return false;
    }
  };

  const handleSubmit = async () => {
    // Reset previous state
    setError('');
    setResult(null);

    // Validation
    if (!url.trim()) {
      setError('Please enter a URL');
      return;
    }

    if (!isValidUrl(url.trim())) {
      setError('Please enter a valid URL (must start with http:// or https://)');
      return;
    }

    setLoading(true);

    try {
      // Add minimum 2-second delay to show loading spinner
      const [response] = await Promise.all([
        urlService.shortenUrl({
          url: url.trim()
        }),
        new Promise(resolve => setTimeout(resolve, 2000))
      ]);
      setResult(response);
      setUrl(''); // Clear the input after successful submission
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to shorten URL');
    } finally {
      setLoading(false);
    }
  };

  const handleNewUrl = () => {
    setResult(null);
    setError('');
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSubmit();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 dark:text-white sm:text-5xl md:text-6xl">
            URL Shortener
          </h1>
          <p className="mt-3 text-xl text-gray-600 dark:text-gray-300 sm:mt-4">
            Transform long URLs into short, shareable links
          </p>
        </div>
        
        {/* Security Notice */}
        <SecurityNotice />
        
        {/* Main Card */}
        <Card className="mb-8">
          <div className="space-y-6">
            <Input
              id="url-input"
              type="url"
              placeholder="https://example.com/very/long/url"
              value={url}
              onChange={setUrl}
              onKeyPress={handleKeyPress}
              error={error}
              label="Enter your long URL"
              disabled={loading}
            />
            
            <Button 
              onClick={handleSubmit}
              loading={loading}
              disabled={!url.trim()}
              size="lg"
              className="w-full"
            >
              Shorten URL
            </Button>
            
            {/* Result Area */}
            {result ? (
              <URLResult result={result} onNewUrl={handleNewUrl} />
            ) : !loading && (
              <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-6 border-2 border-dashed border-gray-300 dark:border-gray-600">
                <p className="text-gray-600 dark:text-gray-400 text-center">
                  Your shortened URL will appear here
                </p>
              </div>
            )}
          </div>
        </Card>
        
        {/* Statistics Panel */}
        <StatsPanel />
        
        {/* Footer */}
        <div className="text-center">
          <p className="text-gray-500 dark:text-gray-400">
            Built with Spring Boot and React
          </p>
        </div>
      </div>
    </div>
  );
}

export default App
