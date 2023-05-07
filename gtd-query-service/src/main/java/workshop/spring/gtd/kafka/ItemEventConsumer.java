package workshop.spring.gtd.kafka;

import workshop.spring.gtd.domain.EventSubscriber;
import workshop.spring.gtd.event.ItemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class ItemEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventSubscriber<ItemEvent> subscriber;

    @Autowired
    public ItemEventConsumer(final EventSubscriber<ItemEvent> subscriber) {
        this.subscriber = subscriber;
    }

    @KafkaListener(topics = "${gtd.topic}", groupId = "${gtd.groupId}")
    public void consume(final ItemEvent itemEvent, final Acknowledgment ack) {
        log.debug("Received event '{}'. Trying to apply it to the latest state of the aggregate with ID '{}'.", itemEvent, itemEvent.getItemId());
        try {
            subscriber.onEvent(itemEvent).thenRun(ack::acknowledge);
        } catch (Exception e) {
            log.warn("Unable to apply event '{}' to the latest state of the aggregate with ID '{}'.", itemEvent, itemEvent.getItemId());
        }
    }
}
