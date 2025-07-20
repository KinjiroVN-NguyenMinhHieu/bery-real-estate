# Step 1: Use OpenJDK image as base
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy the jar file into the container
COPY target/bery-real-estate-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port your app listens to (Render uses PORT env var)
EXPOSE 8080

# Step 5: Set the command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
