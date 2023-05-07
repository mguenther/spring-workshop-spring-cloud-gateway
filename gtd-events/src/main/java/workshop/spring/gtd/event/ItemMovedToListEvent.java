package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemMovedToListEvent extends ItemEvent {

    private final String list;

    public ItemMovedToListEvent(final String itemId,
                                final String list) {
        super(itemId);
        this.list = list;
    }

    @JsonCreator
    public ItemMovedToListEvent(@JsonProperty("list") final String list) {
        this.list = list;
    }

    public String getList() {
        return list;
    }
}
