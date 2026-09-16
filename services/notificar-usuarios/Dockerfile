FROM maven:3.9.14-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY services/pom.xml ./services/pom.xml
COPY services/gerenciador-usuarios/pom.xml ./services/gerenciador-usuarios/pom.xml
COPY services/notificar-usuarios/pom.xml ./services/notificar-usuarios/pom.xml

RUN mvn -q -f pom.xml -pl services/notificar-usuarios -am dependency:go-offline -DskipTests

COPY . .

RUN mvn -q -f pom.xml -pl services/notificar-usuarios -am clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ENV API_PORT_DEFAULT=8092
ENV API_NAME_DEFAULT=notificar-usuarios

COPY --from=build /workspace/services/notificar-usuarios/target/*.jar /app/app.jar

EXPOSE 8092

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
