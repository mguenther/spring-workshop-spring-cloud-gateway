package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ItemClosedEvent extends ItemEvent {

    public ItemClosedEvent(final String itemId) {
        super(itemId);
    }

    @JsonCreator
    public ItemClosedEvent() {
    }
}
