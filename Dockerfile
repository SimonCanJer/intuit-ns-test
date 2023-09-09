FROM openjdk:18-alpine

ARG JAR_NAME=intuit-test
RUN mkdir -p /etc/intuit/sources

COPY target/$JAR_NAME.jar /etc/intuit

COPY sources/player.csv  /etc/intuit/sources

EXPOSE 10101

ENTRYPOINT ["java","-jar","/etc/intuit/intuit_test.jar" ]
