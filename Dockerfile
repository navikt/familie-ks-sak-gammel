FROM navikt/java:17-appdynamics

ENV APPD_ENABLED=true
ENV APP_NAME=familie-ks-sak

COPY ./target/familie-ks-sak.jar "app.jar"
