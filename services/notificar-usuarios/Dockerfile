# ----- 1° ETAPA: BUILD ----- #
FROM maven:3.9.14-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY services/pom.xml ./services/pom.xml
COPY services/gerenciador-usuarios/pom.xml ./services/gerenciador-usuarios/pom.xml
COPY services/notificar-usuarios/pom.xml ./services/notificar-usuarios/pom.xml

COPY services/notificar-usuarios/src ./services/notificar-usuarios/src

RUN mvn -q -f pom.xml -pl services/notificar-usuarios -am clean package -DskipTests

# ----- 2° ETAPA: RUNTIME ----- #
FROM alpine/java:21-jdk

WORKDIR /app

ENV API_PORT_DEFAULT=${API_PORT_DEFAULT}

COPY --from=build /workspace/services/notificar-usuarios/target/*.jar /app/app.jar

EXPOSE ${API_PORT_DEFAULT}

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
