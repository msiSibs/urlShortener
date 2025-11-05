import { useState } from 'react';
import { Button } from './';
import type { ShortenURLResponse } from '../types/api';

interface URLResultProps {
  result: ShortenURLResponse;
  onNewUrl?: () => void;
}

const URLResult: React.FC<URLResultProps> = ({ result, onNewUrl }) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(result.shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      // Fallback for older browsers
      const textArea = document.createElement('textarea');
      textArea.value = result.shortUrl;
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand('copy');
      document.body.removeChild(textArea);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleShare = (platform: string) => {
    const encodedUrl = encodeURIComponent(result.shortUrl);
    const text = encodeURIComponent(`Check out this link: ${result.shortUrl}`);
    
    const shareUrls = {
      twitter: `https://twitter.com/intent/tweet?text=${text}`,
      facebook: `https://www.facebook.com/sharer/sharer.php?u=${encodedUrl}`,
      linkedin: `https://www.linkedin.com/sharing/share-offsite/?url=${text}`,
      whatsapp: `https://wa.me/?text=${text}`,
      email: `mailto:?subject=Shortened URL&body=${text}`,
    };
    
    if (shareUrls[platform as keyof typeof shareUrls]) {
      window.open(shareUrls[platform as keyof typeof shareUrls], '_blank');
    }
  };

  return (
    <div className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-green-800 dark:text-green-200">
          ✅ URL Shortened Successfully!
        </h3>
        {onNewUrl && (
          <Button size="sm" variant="secondary" onClick={onNewUrl}>
            Shorten Another
          </Button>
        )}
      </div>
      
      <div className="space-y-4">
        {/* Short URL Display */}
        <div>
          <label className="block text-sm font-medium text-green-700 dark:text-green-300 mb-2">
            Your shortened URL:
          </label>
          <div className="flex items-center space-x-2">
            <input
              type="text"
              value={result.shortUrl}
              readOnly
              className="flex-1 px-3 py-2 bg-white dark:bg-gray-700 border border-green-300 dark:border-green-600 rounded text-green-800 dark:text-green-200 font-mono text-sm"
            />
            <Button
              size="sm"
              variant={copied ? "secondary" : "primary"}
              onClick={handleCopy}
            >
              {copied ? "Copied!" : "Copy"}
            </Button>
          </div>
        </div>

        {/* URL Details */}
        <div className="bg-green-25 dark:bg-green-900/10 rounded-md p-3 text-sm">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-green-700 dark:text-green-300">
            <div><strong>Short Code:</strong> {result.shortCode}</div>
            <div><strong>Expires:</strong> {new Date(result.expiresAt).toLocaleDateString()}</div>
          </div>
          <div className="mt-2 text-green-600 dark:text-green-400">
            <strong>Original URL:</strong>
            <div className="break-all text-xs mt-1">{result.originalUrl}</div>
          </div>
        </div>

        {/* Social Sharing */}
        <div>
          <label className="block text-sm font-medium text-green-700 dark:text-green-300 mb-2">
            Share your link:
          </label>
          <div className="flex flex-wrap gap-2">
            <Button
              size="sm"
              variant="secondary"
              onClick={() => handleShare('twitter')}
              className="text-blue-500 hover:text-blue-600"
            >
              🐦 Twitter
            </Button>
            <Button
              size="sm"
              variant="secondary"
              onClick={() => handleShare('facebook')}
              className="text-blue-600 hover:text-blue-700"
            >
              📘 Facebook
            </Button>
            <Button
              size="sm"
              variant="secondary"
              onClick={() => handleShare('linkedin')}
              className="text-blue-700 hover:text-blue-800"
            >
              💼 LinkedIn
            </Button>
            <Button
              size="sm"
              variant="secondary"
              onClick={() => handleShare('whatsapp')}
              className="text-green-600 hover:text-green-700"
            >
              💬 WhatsApp
            </Button>
            <Button
              size="sm"
              variant="secondary"
              onClick={() => handleShare('email')}
              className="text-gray-600 hover:text-gray-700"
            >
              ✉️ Email
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default URLResult;