package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemCreatedEvent extends ItemEvent {

    private final String description;

    public ItemCreatedEvent(final String itemId,
                            final String description) {
        super(itemId);
        this.description = description;
    }

    @JsonCreator
    public ItemCreatedEvent(@JsonProperty("description") final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
