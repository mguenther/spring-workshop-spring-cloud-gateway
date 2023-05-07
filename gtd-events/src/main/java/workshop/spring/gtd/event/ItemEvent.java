package workshop.spring.gtd.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DueDateAssignedEvent.class, name = "due-date-assigned-event"),
        @JsonSubTypes.Type(value = ItemClosedEvent.class, name = "item-closed-event"),
        @JsonSubTypes.Type(value = ItemCreatedEvent.class, name = "item-created-event"),
        @JsonSubTypes.Type(value = ItemMovedToListEvent.class, name = "item-moved-to-list-event"),
        @JsonSubTypes.Type(value = RequiredTimeAssignedEvent.class, name = "required-time-assigned-event"),
        @JsonSubTypes.Type(value = TagAssignedEvent.class, name = "tag-assigned-event"),
        @JsonSubTypes.Type(value = TagRemovedEvent.class, name = "tag-removed-event")
})
abstract public class ItemEvent {

    /**
     * This event identifier is unique throughout the system.
     */
    private final String eventId;

    /**
     * The event time is a timestamp in milliseconds since epoch (UTC).
     */
    private final long eventTime;

    /**
     * Uniquely identifies an item.
     */
    private final String itemId;

    public ItemEvent() {
        this(generateItemId());
    }

    public ItemEvent(final String itemId) {
        this(generateEventId(), generateEventTime(), itemId);
    }

    @JsonCreator
    public ItemEvent(@JsonProperty("eventId") final String eventId,
                     @JsonProperty("eventTime") final long eventTime,
                     @JsonProperty("itemId") final String itemId) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.itemId = itemId;
    }

    public String getEventId() {
        return eventId;
    }

    public long getEventTime() {
        return eventTime;
    }

    public String getItemId() {
        return itemId;
    }

    private static String generateItemId() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    private static String generateEventId() {
        return UUID.randomUUID().toString();
    }

    private static long generateEventTime() {
        return Instant.now(Clock.systemUTC()).toEpochMilli();
    }
}
