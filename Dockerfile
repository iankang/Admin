
#Maven Build
FROM maven:3.8.3-openjdk-17-slim AS builder
COPY pom.xml /app/
COPY src /app/src
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml clean package -DskipTests

#Run
FROM bellsoft/liberica-openjdk-alpine:17
COPY --from=builder /app/target/ThinkFusionAUth-0.0.1.jar /opt/app.jar
COPY --from=builder /app/src/main/resources/* /opt/
ADD auth.think.ke.keystore /etc/letsencrypt/live/auth.think.ke/auth.think.ke.keystore.p12
EXPOSE 9081
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=production
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]