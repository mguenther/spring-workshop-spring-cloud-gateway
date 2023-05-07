package workshop.spring.gtd.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ItemEventSerializer implements Serializer<ItemEvent> {

    private final ObjectMapper mapper;

    public ItemEventSerializer() {
        final PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder().build();
        mapper = new ObjectMapper();
        mapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Override
    public byte[] serialize(final String topic, final ItemEvent event) {

        if (event == null) {
            throw new SerializationException("Given record payload must not be null.");
        }

        try {
            return mapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Unable to serialize record payload to byte[].", e);
        }
    }
}
