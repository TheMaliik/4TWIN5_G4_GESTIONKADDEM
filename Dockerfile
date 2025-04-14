FROM openjdk:17-jdk-slim
WORKDIR /app
EXPOSE 8089
ADD  target/kaddem-0.0.1.jar kaddem-0.0.1.jar

ENTRYPOINT ["java", "-jar", "kaddem-0.0.1.jar"]
