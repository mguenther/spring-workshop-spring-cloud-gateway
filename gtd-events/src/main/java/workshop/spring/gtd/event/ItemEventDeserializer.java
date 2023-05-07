package workshop.spring.gtd.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class ItemEventDeserializer implements Deserializer<ItemEvent> {

    private final ObjectMapper mapper;

    public ItemEventDeserializer() {
        final PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder().build();
        mapper = new ObjectMapper();
        mapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Override
    public ItemEvent deserialize(final String topic, final byte[] bytes) {
        try {
            return mapper.readValue(bytes, ItemEvent.class);
        } catch (Exception e) {
            throw new SerializationException("Unable to deserialize record payload into sub-type of item event protocol.", e);
        }
    }
}
