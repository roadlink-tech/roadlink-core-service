FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app

#COPY build.gradle.kts build.gradle.kts
#COPY settings.gradle settings.gradle
#COPY gradlew gradlew
#COPY gradlew.bat gradlew.bat
#COPY api api

COPY api/build/libs/api-0.0.1-SNAPSHOT.jar service.jar

# RUN gradle build --no-daemon

# Etapa 2: Ejecutar la aplicación
FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY --from=build /app/service.jar service.jar
CMD ["java", "-jar", "service.jar"]