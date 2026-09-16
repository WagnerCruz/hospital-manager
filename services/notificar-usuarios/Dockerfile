# ----- 1° ETAPA: BUILD ----- #
FROM maven:3.9.14-eclipse-temurin-21 AS build

WORKDIR /app

COPY services/pom.xml ./services/pom.xml
COPY services/notificar-usuarios/pom.xml ./services/notificar-usuarios/pom.xml

RUN mvn -f services/pom.xml dependency:go-offline

COPY services/notificar-usuarios/src ./services/notificar-usuarios/src

RUN mvn -f services/pom.xml clean package -DskipTests


# ----- 2° ETAPA: RUNTIME ----- #
FROM alpine/java:21-jdk

ENV API_PORT_DEFAULT=${API_PORT_DEFAULT}

WORKDIR /app

COPY --from=build /app/services/notificar-usuarios/target/notificar-usuarios-app.jar ./notificar-usuarios-app.jar

EXPOSE ${API_PORT_DEFAULT}

ENTRYPOINT ["java", "-jar", "notificar-usuarios-app.jar"]