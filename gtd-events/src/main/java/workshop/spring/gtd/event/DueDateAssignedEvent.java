package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DueDateAssignedEvent extends ItemEvent {

    private final Long dueDate;

    public DueDateAssignedEvent(final String itemId,
                                final Long dueDate) {
        super(itemId);
        this.dueDate = dueDate;
    }

    @JsonCreator
    public DueDateAssignedEvent(@JsonProperty("dueDate") final Long dueDate) {
        this.dueDate = dueDate;
    }

    public Long getDueDate() {
        return dueDate;
    }
}
