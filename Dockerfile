FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/pdf-filler-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/main_pdf.pdf /src/main/resources/main_pdf.pdf
COPY src/main/resources/cambria-Bold.ttf /src/main/resources/cambria-Bold.ttf
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]