FROM stain/jena-fuseki:latest
ARG JVM_ARGS=-Xmx4g
COPY ./fuseki-conf/trellis.ttl /fuseki/config.ttl
COPY ./fuseki-conf/shiro.ini /fuseki/shiro.ini
COPY ./fuseki-conf/log4j.properties /fuseki/log4j.properties