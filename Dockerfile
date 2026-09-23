# ---------- Etapa 1: compilación ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Primero solo lo necesario para resolver dependencias (aprovecha la caché de capas)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw dependency:go-offline -B

# Luego el código fuente
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# No ejecutar como root
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]