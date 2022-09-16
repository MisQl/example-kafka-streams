bin/kafka-topics.sh --bootstrap-server localhost:29092 --list
bin/kafka-topics.sh --bootstrap-server localhost:29092 --create --topic example-input-topic
bin/kafka-topics.sh --bootstrap-server localhost:29092 --create --topic example-output-topic

bin/kafka-console-producer.sh --broker-list localhost:29092 --topic example-input-topic --property "parse.key=true" --property "key.separator=:"

bin/kafka-console-consumer.sh --bootstrap-server localhost:29092 --topic example-output-topic \
--from-beginning --formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true --property print.value=true \
--property key.deserialzer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

bin/kafka-topics.sh --zookeeper localhost:2181 localhost:29092 --alter --topic example-output-topic --config retention.ms=5000