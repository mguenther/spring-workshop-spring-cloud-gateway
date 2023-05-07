package workshop.spring.gtd.domain;

import workshop.spring.gtd.event.ItemCreatedEvent;
import workshop.spring.gtd.event.ItemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItemRepository implements EventSubscriber<ItemEvent> {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, Item> items;

    public ItemRepository() {
        this.items = new ConcurrentHashMap<>();
    }

    public CompletableFuture<List<Item>> getItems() {
        return CompletableFuture.supplyAsync(() -> {
            final List<Item> items = new ArrayList<>(this.items.values());
            return Collections.unmodifiableList(items);
        });
    }

    public CompletableFuture<Optional<Item>> getItem(final String itemId) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(items.get(itemId)));
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

    private void createNewItem(final ItemCreatedEvent itemCreated) {
        final Item newItem = new Item(itemCreated);
        items.put(newItem.getId(), newItem);
        log.info("Applied event {} and created new item with current state {}.", itemCreated, newItem);
    }

    private void modifyExistingItem(final ItemEvent event) {
        final Item item = items.get(event.getItemId());
        item.project(event);
        log.info("Applied event {} to the aggregate with ID {} and current state {}.", event, event.getItemId(), item);
    }
}
