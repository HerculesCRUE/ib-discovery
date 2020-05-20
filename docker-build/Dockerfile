FROM openjdk:11

# Copy start script
ADD ./start-script /usr/local
RUN chmod a+x /usr/local/start-container.sh

ENV JAVA_OPTS="-Xmx512M"

CMD /usr/local/start-container.sh

COPY java/app.jar /opt/app/app.jar
