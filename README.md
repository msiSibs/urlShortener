# URL Shortener

A modern URL shortener application built with Java Spring Boot backend and React TypeScript frontend.

## 🚀 Features

**Current Status: Phase 1 Complete - Project Setup & Infrastructure**

- ✅ **Project Architecture**: Complete development environment setup
- ✅ **Docker Ready**: Complete containerized development environment
- ✅ **Health Monitoring**: Basic health check endpoint available
- 🔄 **URL Shortening**: Coming in Phase 2 (Backend Core Development)
- 🔄 **Instant Redirection**: Coming in Phase 2
- 🔄 **Responsive Design**: Coming in Phase 2 (Frontend Implementation)

## 🏗️ Architecture

- **Backend**: Spring Boot 3.3.5 with Java 21
- **Frontend**: React 18 with TypeScript and Vite  
- **Database**: MongoDB for fast NoSQL operations
- **Deployment**: Docker Compose for local development, AWS-ready architecture

## 📋 Development Phases

### ✅ Phase 1: Project Setup & Infrastructure (Complete)
- ✅ Project structure and documentation
- ✅ Docker configuration for all services
- ✅ Development environment setup
- ✅ Basic Spring Boot application with health endpoint
- ✅ React application with TypeScript
- ✅ MongoDB database configuration

### 🔄 Phase 2: Backend Core Development (Next)
- 🔄 URL shortening service implementation
- 🔄 MongoDB repository and models  
- 🔄 RESTful API endpoints
- 🔄 URL validation and expiration logic
- 🔄 Unit and integration tests

### 📋 Phase 3: Frontend Implementation (Planned)
- 📋 React components for URL shortening
- 📋 User interface design
- 📋 API integration
- 📋 Error handling and validation
- 📋 Responsive design

### 📋 Phase 4: Production Ready (Planned)
- 📋 Production Docker configuration
- 📋 CI/CD pipeline activation
- 📋 Performance optimization
- 📋 Security enhancements
- 📋 AWS deployment setup

## 🛠️ Quick Start

### Prerequisites

- **Docker & Docker Compose** (recommended)
- **Java 21+** (if running backend locally)
- **Node.js 18+** (if running frontend locally)
- **MongoDB** (if running database locally)

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/urlShortener.git
   cd urlShortener
   ```

2. **Start all services**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Health Check: http://localhost:8080/health  
   - API Documentation: http://localhost:8080/swagger-ui.html (Coming in Phase 2)
   - MongoDB: Available on host port 27018 (container port 27017)

**Note**: Docker setup uses MongoDB on port 27018 to avoid conflicts with local MongoDB instances.

### Option 2: Local Development

#### Prerequisites for Local Development
- **Java 21+** (Amazon Corretto recommended)
- **Node.js 18+** 
- **MongoDB** running locally on port 27017

#### Setup Steps

1. **Start MongoDB locally**
   ```bash
   # Option 1: Using Docker (if you don't have MongoDB installed)
   docker run -d -p 27017:27017 --name mongodb-local mongo:6
   
   # Option 2: Use your existing local MongoDB installation
   # Ensure MongoDB is running on localhost:27017
   ```

2. **Start Backend with Development Profile**
   ```bash
   cd backend
   
   # Generate Maven wrapper (if not already done)
   mvn wrapper:wrapper
   
   # Run with development profile (connects to localhost:27017)
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Alternative: Set environment variable
   SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
   ```

3. **Start Frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

4. **Access the application**
   - Frontend: http://localhost:5173 (Vite dev server)
   - Backend API: http://localhost:8080
   - Health Check: http://localhost:8080/health
   - API Documentation: http://localhost:8080/swagger-ui.html (Coming in Phase 2)

## 📡 API Endpoints

**Note: The following endpoints are planned for Phase 2 implementation. Currently, only the health check endpoint is available.**

### Health Check (Available Now)
```http
GET /health
```

**Response:**
```json
{
  "status": "UP",
  "service": "URL Shortener Backend",
  "timestamp": "2024-11-04T10:30:00.123Z"
}
```

### Shorten URL (Coming in Phase 2)
```http
POST /api/shorten
Content-Type: application/json

{
  "url": "https://www.example.com/very/long/url",
  "expiresInDays": 30
}
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/aB3dEf",
  "shortCode": "aB3dEf",
  "originalUrl": "https://www.example.com/very/long/url",
  "expiresAt": "2024-11-30T10:30:00Z"
}
```

### Redirect (Coming in Phase 2)
```http
GET /{shortCode}
```

**Response:** `301 Moved Permanently` with `Location` header

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
# Note: Test setup is planned for Phase 2
npm run lint  # Available now for code quality
```

### Integration Tests
```bash
# Integration tests will be implemented in Phase 2
# For now, you can test the health endpoint:
curl http://localhost:8080/health
```

## 📦 Project Structure

```
urlShortener/
├── README.md
├── docker-compose.yml
├── .github/
│   └── workflows/
│       └── ci.yml
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── .gitignore
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│   └── .gitignore
└── docs/
    ├── PROJECT_BREAKDOWN.md
    ├── SYSTEM_ARCHITECTURE.md
    └── DEVELOPMENT_PLAN.md
```

## 🔧 Development

### Environment Variables

The application supports different profiles for various environments:

**Development Profile** (for local development)
```bash
# Backend uses application-dev.properties
SPRING_PROFILES_ACTIVE=dev
# Connects to MongoDB on localhost:27017
```

**Docker Profile** (for Docker Compose)
```bash
# Backend uses application-docker.properties  
SPRING_PROFILES_ACTIVE=docker
# Connects to MongoDB container via service name
```

**Frontend Environment Variables**
Create `.env` file in frontend directory:
```
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=URL Shortener
```

**Configuration Files:**
- `application.properties` - Default configuration (local MongoDB on 27017)
- `application-dev.properties` - Development profile (local MongoDB on 27017, debug logging)  
- `application-docker.properties` - Docker profile (MongoDB container, production-like logging)

### Code Quality

- **Backend**: Uses Spring Boot best practices with comprehensive testing (Phase 2)
- **Frontend**: TypeScript with ESLint configuration
- **Testing**: Unit tests and integration tests planned for Phase 2
- **CI/CD**: GitHub Actions workflow configured but not yet active

## 🚀 Deployment

### Local Production Build
```bash
# Production Docker Compose setup is planned for Phase 3
# Currently available: Development Docker setup
docker-compose up --build
```

### AWS Deployment (Future)
- **Frontend**: AWS S3 + CloudFront
- **Backend**: AWS EC2/ECS with Application Load Balancer
- **Database**: MongoDB Atlas
- **Infrastructure**: AWS CDK/Terraform

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues

**Maven Wrapper Not Found**
```bash
cd backend
# Generate Maven wrapper
mvn wrapper:wrapper
# Then try running again
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**MongoDB Connection Error**
```bash
# Check MongoDB is running
docker ps | grep mongo

# For local development, ensure MongoDB is on port 27017
# For Docker, MongoDB container is mapped to host port 27018

# Restart MongoDB
docker-compose restart mongodb
```

**Port Already in Use**
```bash
# Check what's using the port
lsof -i :8080  # Backend
lsof -i :3000  # Frontend (Docker)
lsof -i :5173  # Frontend (Local Vite)
lsof -i :27017 # Local MongoDB
lsof -i :27018 # Docker MongoDB

# Kill the process
kill -9 <PID>
```

**Build Failures**
```bash
# Clean and rebuild
docker-compose down
docker system prune -f
docker-compose up --build
```

## 📞 Support

For questions and support:
- Create an issue on GitHub