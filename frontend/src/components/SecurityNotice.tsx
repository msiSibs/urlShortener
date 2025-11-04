import { useState } from 'react';
import { Card, Button } from './';

const SecurityNotice: React.FC = () => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isDismissed, setIsDismissed] = useState(false);

  if (isDismissed) return null;

  return (
    <Card className="mb-6 border-amber-200 dark:border-amber-800 bg-amber-50 dark:bg-amber-900/20">
      <div className="flex items-start space-x-3">
        <div className="flex-shrink-0">
          <span className="text-2xl">⚠️</span>
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold text-amber-800 dark:text-amber-200">
              Security Notice
            </h3>
            <div className="flex space-x-2">
              <Button
                size="sm"
                variant="secondary"
                onClick={() => setIsExpanded(!isExpanded)}
                className="text-amber-700 dark:text-amber-300"
              >
                {isExpanded ? 'Hide Details' : 'Learn More'}
              </Button>
              <Button
                size="sm"
                variant="secondary"
                onClick={() => setIsDismissed(true)}
                className="text-amber-700 dark:text-amber-300"
              >
                ✕
              </Button>
            </div>
          </div>

          <p className="text-amber-700 dark:text-amber-300 mt-2">
            <strong>Important:</strong> Only use URL shorteners for public, non-sensitive links. 
            Never shorten URLs containing sensitive information.
          </p>

          {isExpanded && (
            <div className="mt-4 space-y-4">
              {/* What NOT to shorten */}
              <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
                <h4 className="font-semibold text-red-800 dark:text-red-200 mb-2 flex items-center">
                  <span className="mr-2">❌</span>
                  Never Shorten These URLs:
                </h4>
                <ul className="text-sm text-red-700 dark:text-red-300 space-y-1">
                  <li>• URLs with authentication tokens or API keys</li>
                  <li>• Password reset or account verification links</li>
                  <li>• Banking or financial service URLs</li>
                  <li>• Internal company or private system URLs</li>
                  <li>• URLs containing personal identifiers (user IDs, session tokens)</li>
                  <li>• Confidential documents or private file sharing links</li>
                </ul>
              </div>

              {/* What IS safe to shorten */}
              <div className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-4">
                <h4 className="font-semibold text-green-800 dark:text-green-200 mb-2 flex items-center">
                  <span className="mr-2">✅</span>
                  Safe to Shorten:
                </h4>
                <ul className="text-sm text-green-700 dark:text-green-300 space-y-1">
                  <li>• Public news articles and blog posts</li>
                  <li>• Social media content and profiles</li>
                  <li>• Public product pages and marketing content</li>
                  <li>• Educational resources and documentation</li>
                  <li>• Open-source projects and repositories</li>
                  <li>• Public event pages and announcements</li>
                </ul>
              </div>

              {/* Why this matters */}
              <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
                <h4 className="font-semibold text-blue-800 dark:text-blue-200 mb-2 flex items-center">
                  <span className="mr-2">🔍</span>
                  Why This Matters:
                </h4>
                <ul className="text-sm text-blue-700 dark:text-blue-300 space-y-1">
                  <li>• Original URLs are stored in plaintext in our database</li>
                  <li>• Server logs may contain your original URLs</li>
                  <li>• Database administrators can see all shortened URLs</li>
                  <li>• This is a public service - treat it like posting publicly</li>
                </ul>
              </div>

              {/* Best practices */}
              <div className="bg-purple-50 dark:bg-purple-900/20 border border-purple-200 dark:border-purple-800 rounded-lg p-4">
                <h4 className="font-semibold text-purple-800 dark:text-purple-200 mb-2 flex items-center">
                  <span className="mr-2">💡</span>
                  Best Practices:
                </h4>
                <ul className="text-sm text-purple-700 dark:text-purple-300 space-y-1">
                  <li>• Use direct sharing for sensitive links</li>
                  <li>• Consider enterprise URL shorteners for internal use</li>
                  <li>• Check URLs for sensitive parameters before shortening</li>
                  <li>• Use temporary sharing services for confidential content</li>
                  <li>• When in doubt, don't shorten - share directly</li>
                </ul>
              </div>

              <div className="text-xs text-amber-600 dark:text-amber-400 mt-4 italic">
                Future versions may include URL encryption and enhanced privacy features. 
                Current version stores URLs in plaintext for service functionality.
              </div>
            </div>
          )}
        </div>
      </div>
    </Card>
  );
};

export default SecurityNotice;