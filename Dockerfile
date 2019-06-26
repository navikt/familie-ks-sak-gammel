FROM navikt/java:11-appdynamics

ENV APPD_ENABLED=TRUE

COPY ./target/familie-ks-sak.jar "app.jar"
