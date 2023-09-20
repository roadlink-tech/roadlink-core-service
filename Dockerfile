FROM gradle:7.3.3-jdk17 AS build

WORKDIR /app

# copy source code
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY gradlew gradlew
COPY gradle gradle
COPY api api

# TODO use gradle instead of gradlew. GRadle version should be 8.2.1
RUN ./gradlew clean build -x test

# Etapa 2: Ejecutar la aplicación
FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY --from=build /app/api/build/libs/*-SNAPSHOT.jar service.jar
CMD ["java", "-jar", "service.jar"]