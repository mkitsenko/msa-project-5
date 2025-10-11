FROM openjdk:24-slim
# 8080
WORKDIR .
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]