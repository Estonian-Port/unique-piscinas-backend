# Etapa 1: Build con Gradle Wrapper
FROM gradle:8.14-jdk17-alpine AS builder

WORKDIR /home/app

# Copiamos archivos de configuración y wrapper
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Damos permisos de ejecución al wrapper
RUN chmod +x ./gradlew

# Genera el .jar optimizado
RUN ./gradlew bootJar --no-daemon

# Etapa 2: Imagen liviana para correr la app
FROM eclipse-temurin:17-jre-alpine

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY --from=builder /home/app/build/libs/*.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]
