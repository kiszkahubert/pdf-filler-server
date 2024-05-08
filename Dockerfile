FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/pdf-filler-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/main_pdf.pdf /src/main/resources/main_pdf.pdf
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]