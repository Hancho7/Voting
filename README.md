# 🗳️ VotingSystem

A secure, modern voting platform built with Spring Boot, featuring Google OAuth2 authentication and robust JWT token management. Designed for scalability, security, and ease of use.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## ✨ Key Features

- 🔐 **Secure Authentication** - Google OAuth2 integration with custom JWT implementation
- 🔑 **RSA Token Signing** - Industry-standard RSA key pair for JWT security
- 👤 **User Management** - Automatic user registration and profile management
- 🛡️ **Comprehensive Security** - Spring Security 6.x with CORS support
- 🗄️ **Database Integration** - PostgreSQL with JPA/Hibernate
- 🚀 **RESTful API** - Clean, well-structured REST endpoints
- ⚡ **Global Exception Handling** - Centralized error management
- 📊 **Production Ready** - Built for scalability and reliability

## 🚀 Quick Start

### Prerequisites

- **Java 21**
- **PostgreSQL 12+**
- **Maven 3.6+**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/VotingSystem.git
   cd VotingSystem
   ```

2. **Setup PostgreSQL database**
   ```bash
   createdb voting_system
   ```

3. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your database credentials
   ```

4. **Generate RSA keys for JWT**
   ```bash
   mkdir -p src/main/resources/keys

   # Generate private key
   openssl genrsa -out src/main/resources/keys/private_key.pem 2048

   # Convert to PKCS#8 format (Java-compatible)
   openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \
     -in src/main/resources/keys/private_key.pem \
     -out src/main/resources/keys/private_key_pkcs8.pem

   # Generate public key
   openssl rsa -in src/main/resources/keys/private_key.pem \
     -pubout -out src/main/resources/keys/public_key.pem

   # Replace with PKCS#8 version
   mv src/main/resources/keys/private_key_pkcs8.pem src/main/resources/keys/private_key.pem

   # Set permissions
   chmod 600 src/main/resources/keys/private_key.pem
   chmod 644 src/main/resources/keys/public_key.pem
   ```

5. **Run the application**
   you can just edit the makefile with the necessary credentials and run Make run to avoid the steps above 
   ```bash
   make run
   # or
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 🔧 Environment Configuration

Create a `.env` file in the project root:

```env
DB_URL=jdbc:postgresql://localhost:5432/yourdatabasename
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
```

## 📖 API Usage

### Authentication

**Authenticate with Google OAuth2:**
```bash
curl -X POST http://localhost:8080/auth/google \
  -H "Content-Type: application/json" \
  -d '{"googleToken": "your_google_oauth2_token"}'
```

**Response:**
```json
{
  "accessToken": "jwt_access_token",
  "refreshToken": "jwt_refresh_token",
  "expiresAt": "2025-06-28T15:30:00.000Z",
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "department": null
}
```

### Protected Endpoints

**Get current user:**
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer your_jwt_token"
```

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│           Controller Layer          │
│  (AuthController, UserController)   │
├─────────────────────────────────────┤
│            Service Layer            │
│  (UserService, JwtService, etc.)    │
├─────────────────────────────────────┤
│         Repository Layer            │
│      (UsersRepository)              │
├─────────────────────────────────────┤
│          Database Layer             │
│         (PostgreSQL)                │
└─────────────────────────────────────┘
```

**Security Flow:**
```
Client → Security Filter → JWT Filter → Controller → Service → Repository → Database
```

## 🛠️ Technology Stack

| Category | Technology |
|----------|------------|
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security 6.x |
| **Database** | PostgreSQL + JPA/Hibernate |
| **Authentication** | Google OAuth2 + Custom JWT |
| **Build Tool** | Maven |
| **Java Version** | 21 |

## 📚 Documentation

- **[Architecture Guide](docs/ARCHITECTURE.md)** - Technical deep dive and system design
- **[API Reference](docs/API.md)** - Complete API documentation with examples
- **[Development Guide](docs/DEVELOPMENT.md)** - Setup, coding guidelines, and best practices

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📦 Building

```bash
# Clean and compile
./mvnw clean compile

# Package for production
./mvnw package

# Build Docker image (if configured)
docker build -t voting-system .
```

## 🚀 Deployment

The application can be deployed using:

- **JAR file** - `java -jar target/VotingSystem-*.jar`
- **Docker** - See [Deployment Guide](docs/DEPLOYMENT.md)
- **Cloud platforms** - AWS, GCP, Azure compatible

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📋 Roadmap

- [ ] **Voting Core Features** - Poll creation and voting logic
- [ ] **Role-Based Access Control** - Admin, voter, observer roles
- [ ] **Real-time Updates** - WebSocket integration
- [ ] **Audit Trail** - Vote tracking and verification
- [ ] **Analytics Dashboard** - Vote results and reporting
- [ ] **Mobile API** - Enhanced mobile support

## 🐛 Issues & Support

- **Bug Reports**: [GitHub Issues](https://github.com/Hancho7/VotingSystem/issues)
- **Feature Requests**: [GitHub Discussions](https://github.com/Hancho7/VotingSystem/discussions)
- **Documentation**: Check our [docs folder](docs/)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) for the excellent framework
- [Google OAuth2](https://developers.google.com/identity/protocols/oauth2) for secure authentication
- [PostgreSQL](https://www.postgresql.org/) for reliable data storage

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made with ❤️ by [Jose Jefferson](https://github.com/Hancho7)

</div>
