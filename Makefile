.PHONY: setup-keys clean-keys run test build docker-build

# Generate RSA keys for JWT signing
setup-keys:
	@echo "🔐 Generating RSA key pair for JWT signing..."
	@mkdir -p src/main/resources/keys
	@openssl genrsa -out src/main/resources/keys/private_key.pem 2048
	@openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \
		-in src/main/resources/keys/private_key.pem \
		-out src/main/resources/keys/private_key_pkcs8.pem
	@openssl rsa -in src/main/resources/keys/private_key.pem \
		-pubout -out src/main/resources/keys/public_key.pem
	@mv src/main/resources/keys/private_key_pkcs8.pem src/main/resources/keys/private_key.pem
	@chmod 600 src/main/resources/keys/private_key.pem
	@chmod 644 src/main/resources/keys/public_key.pem
	@echo "✅ RSA keys generated successfully!"

# Clean generated keys
clean-keys:
	@echo "🧹 Cleaning up RSA keys..."
	@rm -rf src/main/resources/keys/
	@echo "✅ Keys cleaned!"

# Setup environment for development
setup-dev: setup-keys
	@echo "🚀 Setting up development environment..."
	@if [ ! -f .env ]; then \
		echo "📝 Creating .env template..."; \
		echo "DB_URL=jdbc:postgresql://localhost:5432/voting_system" > .env; \
		echo "DB_USERNAME=username" >> .env; \
		echo "DB_PASSWORD=password" >> .env; \
		# echo "⚠️  Please update .env with your actual database credentials!"; \
	fi
	@echo "✅ Development environment ready!"

# Run application (includes key check)
run: check-keys
	@export $$(grep -v '^#' .env | xargs) && ./mvnw spring-boot:run

# Check if keys exist, generate if not
check-keys:
	@if [ ! -f src/main/resources/keys/private_key.pem ]; then \
		echo "🔑 RSA keys not found. Generating..."; \
		$(MAKE) setup-keys; \
	fi

# Build application
build: check-keys
	@echo "🔨 Building application..."
	@./mvnw clean package -DskipTests
	@echo "✅ Build completed!"

# Run tests
test: check-keys
	@echo "🧪 Running tests..."
	@./mvnw test
	@echo "✅ Tests completed!"

# Docker build (for containerization)
# docker-build: build
# 	@echo "🐳 Building Docker image..."
# 	@docker build -t voting-system:latest .
# 	@echo "✅ Docker image built!"
#
# Clean everything
clean: clean-keys
	@echo "🧹 Cleaning project..."
	@./mvnw clean
	@rm -f .env
	@echo "✅ Project cleaned!"

# Help command
help:
	@echo "🚀 VotingSystem - Available Commands:"
	@echo ""
	@echo "Development Commands:"
	@echo "  setup-dev     - Setup development environment (keys + .env template)"
	@echo "  setup-keys    - Generate RSA key pair for JWT signing"
	@echo "  run          - Run the application with environment variables"
	@echo "  test         - Run all tests"
	@echo "  build        - Build the application"
	@echo ""
	@echo "Utility Commands:"
	@echo "  check-keys   - Check if keys exist, generate if missing"
	@echo "  clean-keys   - Remove generated RSA keys"
	@echo "  clean        - Clean project and remove all generated files"
	@echo "  docker-build - Build Docker image"
	@echo "  help         - Show this help message"
