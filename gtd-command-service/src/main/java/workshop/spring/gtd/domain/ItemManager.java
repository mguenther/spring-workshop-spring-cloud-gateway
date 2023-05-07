package workshop.spring.gtd.domain;

import workshop.spring.gtd.domain.command.AssignDueDate;
import workshop.spring.gtd.domain.command.AssignRequiredTime;
import workshop.spring.gtd.domain.command.AssignTags;
import workshop.spring.gtd.domain.command.CloseItem;
import workshop.spring.gtd.domain.command.CreateItem;
import workshop.spring.gtd.domain.command.ItemCommand;
import workshop.spring.gtd.domain.command.MoveItemToList;
import workshop.spring.gtd.event.DueDateAssignedEvent;
import workshop.spring.gtd.event.ItemClosedEvent;
import workshop.spring.gtd.event.ItemCreatedEvent;
import workshop.spring.gtd.event.ItemEvent;
import workshop.spring.gtd.event.ItemMovedToListEvent;
import workshop.spring.gtd.event.RequiredTimeAssignedEvent;
import workshop.spring.gtd.event.TagAssignedEvent;
import workshop.spring.gtd.event.TagRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Service
public class ItemManager implements CommandHandler<ItemCommand>, EventSubscriber<ItemEvent> {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, Item> items;

    private final EventPublisher<ItemEvent> publisher;

    @Autowired
    public ItemManager(final EventPublisher<ItemEvent> publisher) {
        this.publisher = publisher;
        this.items = new ConcurrentHashMap<>();
    }

    @Override
    public CompletableFuture<Void> onCommand(final ItemCommand command) {
        return CompletableFuture.runAsync(() -> validate(command).forEach(publisher::log));
    }

    @Override
    public CompletableFuture<Void> onCommand(final List<ItemCommand> commands) {
        return CompletableFuture.runAsync(() -> commands
                .stream()
                .flatMap(command -> validate(command).stream())
                .forEach(publisher::log));
    }

    private List<ItemEvent> validate(final ItemCommand command) {
        if (command instanceof CreateItem) {
            return validate((CreateItem) command);
        }

        final Item item = items.get(command.getItemId());
        if (item == null) {
            log.warn("Item with ID {} does not exist but has received a command ({}). This is probably due to a " +
                            "out-of-order arrival of events, which should not happen due to Kafka's topic ordering guarantees. " +
                            "Please check your Kafka topic configuration. The command will be dropped.",
                    command.getItemId(), command);
            return emptyList();
        }

        if (command instanceof AssignDueDate) return validate(item, (AssignDueDate) command);
        else if (command instanceof AssignRequiredTime) return validate(item, (AssignRequiredTime) command);
        else if (command instanceof AssignTags) return validate(item, (AssignTags) command);
        else if (command instanceof CloseItem) return validate(item, (CloseItem) command);
        else if (command instanceof MoveItemToList) return validate(item, (MoveItemToList) command);
        else return emptyList();
    }

    private List<ItemEvent> validate(final CreateItem command) {
        return singletonList(new ItemCreatedEvent(command.getItemId(), command.getDescription()));
    }

    private List<ItemEvent> validate(final Item item, final AssignDueDate command) {
        final Date now = Date.from(Instant.now(Clock.systemUTC()));
        if (item.isDone() || command.getDueDate().before(now)) {
            logValidationFailed(item, command);
            return emptyList();
        } else {
            return singletonList(new DueDateAssignedEvent(item.getId(), command.getDueDate().getTime()));
        }
    }

    private List<ItemEvent> validate(final Item item, final AssignRequiredTime command) {
        if (item.isDone() || command.getRequiredTime() < 0) {
            logValidationFailed(item, command);
            return emptyList();
        } else {
            return singletonList(new RequiredTimeAssignedEvent(item.getId(), command.getRequiredTime()));
        }
    }

    private List<ItemEvent> validate(final Item item, final AssignTags command) {

        if (item.isDone()) {
            logValidationFailed(item, command);
            return emptyList();
        }

        final List<ItemEvent> events = new ArrayList<>();
        events.addAll(command.getTags()
                .stream()
                .filter(tag -> !item.getTags().contains(tag))
                .map(tag -> new TagAssignedEvent(command.getItemId(), tag))
                .toList());
        events.addAll(item.getTags()
                .stream()
                .filter(tag -> !command.getTags().contains(tag))
                .map(tag -> new TagRemovedEvent(command.getItemId(), tag))
                .toList());
        return events;
    }

    private List<ItemEvent> validate(final Item item, final CloseItem command) {
        if (item.isDone()) {
            logValidationFailed(item, command);
            return emptyList();
        } else {
            return singletonList(new ItemClosedEvent(item.getId()));
        }
    }

    private List<ItemEvent> validate(final Item item, final MoveItemToList command) {
        if (item.isDone() || command.getList().equals(item.getAssociatedList())) {
            logValidationFailed(item, command);
            return emptyList();
        } else {
            return singletonList(new ItemMovedToListEvent(item.getId(), command.getList()));
        }
    }

    private void logValidationFailed(final Item currentState, final ItemCommand command) {
        log.warn("Received command {} which failed to validate against the current state of aggregate {}. " +
                "Skipping this command.", command, currentState);
    }

    @Override
    public CompletableFuture<Void> onEvent(final ItemEvent event) {

        return CompletableFuture.runAsync(() -> {

            if (event instanceof ItemCreatedEvent) {
                createNewItem((ItemCreatedEvent) event);
            } else {
                modifyExistingItem(event);
            }
        });
    }

    private void createNewItem(final ItemCreatedEvent event) {
        final Item newItem = new Item(event);
        items.put(newItem.getId(), newItem);
    }

    private void modifyExistingItem(final ItemEvent event) {
        final Item currentState = items.get(event.getItemId());

        if (currentState == null) {
            throw new IllegalStateException("Event " + event.toString() + " cannot be applied. There is no state for item with ID " + event.getItemId() + ".");
        }

        currentState.project(event);
        items.put(currentState.getId(), currentState);
    }
}
