FROM maven:3.9.4-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package

CMD ["java", "-jar", "target/line-wedding-bot-0.0.1-SNAPSHOT.jar"]