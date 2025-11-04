// MongoDB initialization script
print('Starting MongoDB initialization...');

// Switch to urlshortener database
db = db.getSiblingDB('urlshortener');

// Create url_mappings collection with validation
db.createCollection('url_mappings', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['shortCode', 'originalUrl', 'createdAt'],
      properties: {
        shortCode: {
          bsonType: 'string',
          pattern: '^[A-Za-z0-9]{6,8}$',
          description: 'Short code must be 6-8 alphanumeric characters'
        },
        originalUrl: {
          bsonType: 'string',
          pattern: '^https?://.+',
          description: 'Original URL must be a valid HTTP/HTTPS URL'
        },
        createdAt: {
          bsonType: 'date',
          description: 'Creation timestamp is required'
        },
        expiresAt: {
          bsonType: 'date',
          description: 'Optional expiration timestamp'
        },
        domain: {
          bsonType: 'string',
          description: 'Extracted domain from original URL'
        }
      }
    }
  }
});

// Create indexes for optimal performance
db.url_mappings.createIndex({ 'shortCode': 1 }, { unique: true });
db.url_mappings.createIndex({ 'expiresAt': 1 }, { expireAfterSeconds: 0 });
db.url_mappings.createIndex({ 'createdAt': -1 });
db.url_mappings.createIndex({ 'domain': 1 });

print('Created url_mappings collection with indexes');

// Insert sample data for testing (optional)
db.url_mappings.insertOne({
  shortCode: 'sample1',
  originalUrl: 'https://www.example.com',
  createdAt: new Date(),
  expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000), // 1 year
  domain: 'example.com'
});

print('MongoDB initialization completed successfully');
print('Collections created:', db.getCollectionNames());
print('Indexes on url_mappings:', db.url_mappings.getIndexes());