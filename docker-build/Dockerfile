FROM openjdk:11

# Copy start script
ADD ./start-script /usr/local
RUN chmod a+x /usr/local/start-container.sh

ENV JAVA_OPTS="-Xmx512M"
ENV app_persistence_datasource_url="jdbc:mariadb://host.docker.internal:3307/discovery?ssl=false&createDatabaseIfNotExist=true"
ENV data_elasticsearch_host="host.docker.internal"
ENV data_elasticsearch_port="9200"
ENV data_redis_host="host.docker.internal"
ENV data_redis_port="16379"
ENV data_redis_password="redispass"
ENV spring_kafka_bootstrap-servers="host.docker.internal:9092"
ENV spring_kafka_consumer-group-id="myGroup"
ENV data_kafka_host="host.docker.internal"
ENV data_kafka_port="9092"
ENV data_kafka_topicEntityChange_topic="entity_change"
ENV data_kafka_topicEntityChange_groupId="entity_change_1"
ENV data_kafka_topicDiscoveryAction_topic="discovery_action"
ENV data_kafka_topicEntityChange_groupId="discovery_action_1"

CMD /usr/local/start-container.sh

COPY java/*.jar /opt/app/app.jar
