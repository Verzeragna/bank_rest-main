FROM eclipse-temurin:21
EXPOSE 8010
ARG JAR_FILE=target/*.jar
WORKDIR /opt/app
COPY ${JAR_FILE} bankcards-1.0.jar
ENTRYPOINT ["java","-jar","bankcards-1.0.jar"]