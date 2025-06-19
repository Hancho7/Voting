run:
	# Load environment variables from .env and run the Spring Boot application
	export $$(grep -v '^#' .env | xargs) && ./mvnw spring-boot:run
