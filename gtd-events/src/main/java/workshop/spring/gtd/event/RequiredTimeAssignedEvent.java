package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequiredTimeAssignedEvent extends ItemEvent {

    private final Integer requiredTime;

    public RequiredTimeAssignedEvent(final String itemId,
                                     final Integer requiredTime) {
        this.requiredTime = requiredTime;
    }

    @JsonCreator
    public RequiredTimeAssignedEvent(@JsonProperty("requiredTime") final Integer requiredTime) {
        this.requiredTime = requiredTime;
    }

    public Integer getRequiredTime() {
        return requiredTime;
    }
}
