FROM openjdk:11.0.2-jre-slim-stretch

WORKDIR /opt

COPY target/*-exec.jar /opt/app.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar