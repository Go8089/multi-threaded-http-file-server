# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
# Extract Spring Boot layers
RUN java -Djarmode=layertools -jar target/*.jar extract

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /safeNest
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./

EXPOSE 8080
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]