FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN mvn clean package

CMD ["java", "-jar", "target/line-wedding-bot-0.0.1-SNAPSHOT.jar"]