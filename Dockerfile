FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

ARG MODULE

COPY pom.xml /app/pom.xml
COPY jwt-handler/pom.xml /app/jwt-handler/pom.xml

COPY jwt-handler/src /app/jwt-handler/src
RUN mvn -f /app/jwt-handler/pom.xml clean install -B

COPY ${MODULE}/pom.xml /app/${MODULE}/pom.xml
RUN mvn -f /app/${MODULE}/pom.xml dependency:go-offline -B

COPY ${MODULE}/src /app/${MODULE}/src
RUN mvn -f /app/${MODULE}/pom.xml clean package -B


FROM eclipse-temurin:25-jre

WORKDIR /app

ARG MODULE
ARG APP_PORT

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/${MODULE}/target/*.jar app.jar

EXPOSE ${APP_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
