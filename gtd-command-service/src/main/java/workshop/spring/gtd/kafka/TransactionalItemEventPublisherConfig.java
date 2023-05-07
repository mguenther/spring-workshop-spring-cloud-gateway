package workshop.spring.gtd.kafka;

import workshop.spring.gtd.event.ItemEvent;
import workshop.spring.gtd.event.ItemEventSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TransactionalItemEventPublisherConfig {

    @Value("${spring.kafka.producer.bootstrap-servers:localhost:9092}")
    String bootstrapServers;

    @Bean
    public ProducerFactory<String, ItemEvent> producerFactory() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ItemEventSerializer.class);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        config.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "getting-things-done");
        final DefaultKafkaProducerFactory<String, ItemEvent> factory =
                new DefaultKafkaProducerFactory<>(config);
        factory.setTransactionIdPrefix("getting-things-done");
        return factory;
    }

    @Bean
    public KafkaTemplate<String, ItemEvent> kafkaTemplate(@Autowired ProducerFactory<String, ItemEvent> factory) {
        return new KafkaTemplate<>(factory);
    }
}
