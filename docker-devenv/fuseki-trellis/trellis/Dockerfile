FROM openjdk:11

# Copy start script
ADD ./start-script /usr/local
RUN chmod a+x /usr/local/start-container.sh

ENV JAVA_OPTS="-Xmx512M"
ENV ASIO_LDP_CONFIG_FILE="/opt/app/config-dev.yml"

CMD /usr/local/start-container.sh

COPY asio-ldp-dropwizard-1.0-SNAPSHOT-shaded.jar /opt/app/asio-ldp-dropwizard-1.0-SNAPSHOT-shaded.jar
COPY config-dev.yml /opt/app/config-dev.yml
