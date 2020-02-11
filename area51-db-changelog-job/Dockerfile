FROM openjdk:11.0.2-jre-slim-stretch

WORKDIR /opt

COPY target/*-exec.jar /opt/app.jar

CMD ["java", "-jar", "app.jar"]