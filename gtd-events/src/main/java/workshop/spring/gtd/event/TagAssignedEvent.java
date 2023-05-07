package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TagAssignedEvent extends ItemEvent {

    private final String tag;

    public TagAssignedEvent(final String itemId,
                            final String tag) {
        super(itemId);
        this.tag = tag;
    }

    @JsonCreator
    public TagAssignedEvent(@JsonProperty("tag") final String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
