package workshop.spring.gtd.kafka;

import workshop.spring.gtd.domain.EventPublisher;
import workshop.spring.gtd.event.ItemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionalItemEventPublisher implements EventPublisher<ItemEvent> {

    private static final Logger log = LoggerFactory.getLogger(TransactionalItemEventPublisher.class);

    private final String topicName;

    private final KafkaTemplate<String, ItemEvent> kafkaTemplate;

    @Autowired
    public TransactionalItemEventPublisher(@Value("${gtd.topic:topic-getting-things-done}") final String topicName,
                                           final KafkaTemplate<String, ItemEvent> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void log(final ItemEvent event) {
        log.info("Attempting to log {} to topic {}.", event, topicName);
        kafkaTemplate.executeInTransaction(operations -> {
            final String key = event.getItemId();
            operations
                    .send(topicName, key, event)
                    .thenAccept(result -> {
                        log.info("ItemEvent '{}' has been written to topic-partition {}-{} with ingestion timestamp {}.",
                                result.getProducerRecord().key(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().timestamp());
                    });
            return true;
        });
    }
}
