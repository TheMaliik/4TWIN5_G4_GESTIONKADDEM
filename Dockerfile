FROM openjdk:11-jdk-rima
WORKDIR /app
EXPOSE 8089
ADD  target/kaddem-0.0.1.jar kaddem-0.0.1.jar

ENTRYPOINT ["java", "-jar", " kaddem-0.0.1.jar"]
