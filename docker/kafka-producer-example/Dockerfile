FROM java:8-jre-alpine

WORKDIR /app

COPY producer.jar .
COPY oauth-configuration.properties .

ENV KAFKA_OAUTH_SERVER_PROP_FILE=/app/oauth-configuration.properties

CMD [ "java", "-jar", "/app/producer.jar" ]