# =============================================================
# wsci-cloud-service-immobilier — Dockerfile multi-stage Java 21
# Image finale : eclipse-temurin:21-jre-alpine (~95 MB)
# =============================================================

# -----------------------------------------------------------
# STAGE 1 : Build Maven (cache des dépendances séparé)
# -----------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# Copier le wrapper Maven en premier (cache Docker optimisé)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Télécharger les dépendances sans compiler le code source
RUN ./mvnw dependency:go-offline -B --no-transfer-progress -q

# Copier les sources et construire
COPY src ./src
RUN ./mvnw package -DskipTests -B --no-transfer-progress -q

# -----------------------------------------------------------
# STAGE 2 : Extraction du JAR en couches Spring Boot
# -----------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS extractor

WORKDIR /workspace/extracted

COPY --from=builder /workspace/target/wsci-cloud-service-immobilier.jar app.jar

# Extraction des couches (dependencies / spring-boot-loader / application)
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination .

# -----------------------------------------------------------
# STAGE 3 : Image finale JRE Alpine (production)
# -----------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Sécurité : utilisateur non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copier les couches dans l'ordre de stabilité (plus stables en premier)
COPY --from=extractor /workspace/extracted/dependencies/          ./
COPY --from=extractor /workspace/extracted/spring-boot-loader/   ./
COPY --from=extractor /workspace/extracted/snapshot-dependencies/ ./
COPY --from=extractor /workspace/extracted/application/          ./

# Options JVM optimisées pour conteneur Kubernetes
ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:InitialRAMPercentage=50.0 \
  -XX:+UseZGC \
  -XX:+ZGenerational \
  -XX:+ExitOnOutOfMemoryError \
  -Xss256k \
  -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

# Health check via Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health/readiness || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

