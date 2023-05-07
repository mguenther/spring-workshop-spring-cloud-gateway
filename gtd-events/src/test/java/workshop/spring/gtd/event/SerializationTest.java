package workshop.spring.gtd.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import workshop.spring.gtd.event.ItemClosedEvent;

class SerializationTest {

    @Test
    void test() throws Exception {
        var mapper = new ObjectMapper();
        var event = new ItemClosedEvent("some-id");

        var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);

        System.out.println(json);

        var sameEvent = mapper.readValue(json, ItemClosedEvent.class);

        System.out.println("---------------");
        System.out.println(sameEvent.getEventId());
        System.out.println(sameEvent.getEventTime());
        System.out.println(sameEvent.getItemId());
        //System.out.println(sameEvent.getDueDate());
    }
}
