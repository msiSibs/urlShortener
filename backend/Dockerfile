# Build stage
FROM maven:3.9.11-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Download dependencies and build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21.0.9

WORKDIR /app

# Install curl for health check
RUN yum update -y && yum install -y curl && yum clean all

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# TODO: Re-enable for production security
# Create non-root user for security
# RUN addgroup --system spring && adduser --system spring --ingroup spring
# USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]