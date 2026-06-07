FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml /app/pom.xml
COPY crm/pom.xml /app/crm/pom.xml
RUN mvn dependency:go-offline -B

COPY crm/src /app/crm/src

RUN mvn -pl crm -am clean package


FROM eclipse-temurin:25-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/crm/target/crm-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8020

ENTRYPOINT ["java", "-jar", "app.jar"]
