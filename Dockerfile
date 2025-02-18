# Use a smaller OpenJDK runtime image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR into the container
# (which should be built previously, `gradle shadowJar`, say in GitHub Actions)
COPY JavaFXStub/build/libs/JavaFXStub-0.0.1-all.jar /app/app.jar

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Expose the application port (if needed, adjust the port number)
#EXPOSE 8080