# ----- 1° ETAPA: BUILD ----- #
FROM maven:3.9.14-eclipse-temurin-21 AS build

WORKDIR /app

COPY services/pom.xml ./services/pom.xml
COPY services/gerenciador-usuarios/pom.xml ./services/gerenciador-usuarios/pom.xml

RUN mvn -f services/pom.xml dependency:go-offline

COPY services/gerenciador-usuarios/src ./services/gerenciador-usuarios/src

RUN mvn -f services/pom.xml clean package -DskipTests

# ----- 2° ETAPA: RUNTIME ----- #
FROM alpine/java:21-jdk

ENV API_PORT_DEFAULT=${API_PORT_DEFAULT}

WORKDIR /app

COPY --from=build /app/services/gerenciador-usuarios/target/gerenciador-usuarios-app.jar ./gerenciador-usuarios-app.jar

EXPOSE ${API_PORT_DEFAULT}

ENTRYPOINT ["java", "-jar", "gerenciador-usuarios-app.jar"]