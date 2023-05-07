package workshop.spring.gtd.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import workshop.spring.gtd.domain.CommandHandler;
import workshop.spring.gtd.domain.command.AssignDueDate;
import workshop.spring.gtd.domain.command.AssignRequiredTime;
import workshop.spring.gtd.domain.command.AssignTags;
import workshop.spring.gtd.domain.command.CloseItem;
import workshop.spring.gtd.domain.command.ItemCommand;
import workshop.spring.gtd.domain.command.MoveItemToList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
public class ItemCommandResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CommandHandler<ItemCommand> handler;

    @Autowired
    public ItemCommandResource(final CommandHandler<ItemCommand> handler) {
        this.handler = handler;
    }

    @Operation(
            summary = "${api.close-item.description}",
            description = "${api.close-item.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "${api.response-codes.accepted.description}"),
            @ApiResponse(responseCode = "404", description = "${api.response-codes.notFound.description}"),
            @ApiResponse(responseCode = "500", description = "${api.response-codes.internalServerError.description}")
    })
    @DeleteMapping(path = "/api/items/{itemId}", produces = "application/json")
    public CompletableFuture<ResponseEntity<Object>> closeItem(@PathVariable("itemId") String itemId) {

        log.info("Received a delete item request for item with ID {}.", itemId);

        return handler
                .onCommand(new CloseItem(itemId))
                .thenApply(dontCare -> ResponseEntity.accepted().build())
                .exceptionally(e -> {
                    log.warn("Caught an exception at the service boundary.", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Operation(
            summary = "${api.update-item.description}",
            description = "${api.update-item.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "${api.response-codes.accepted.description}"),
            @ApiResponse(responseCode = "404", description = "${api.response-codes.notFound.description}"),
            @ApiResponse(responseCode = "500", description = "${api.response-codes.internalServerError.description}")
    })
    @PutMapping(path = "/api/items/{itemId}", consumes = "application/json", produces = "application/json")
    public CompletableFuture<ResponseEntity<Object>> updateItem(@PathVariable("itemId") String itemId,
                                                                @RequestBody UpdateItemRequest updateItem) {

        log.info("Received an update item request for item with ID {} and updated data {}.", itemId, updateItem);

        return handler
                .onCommand(commandsFor(itemId, updateItem).orElse(Collections.emptyList()))
                .thenApply(dontCare -> ResponseEntity.accepted().build())
                .exceptionally((e -> {
                    log.warn("Caught an exception at the service boundary.", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }));
    }

    private Optional<List<ItemCommand>> commandsFor(final String itemId, final UpdateItemRequest updateItem) {
        final List<ItemCommand> commands = new ArrayList<>();
        if (updateItem.getAssociatedList() != null) {
            commands.add(new MoveItemToList(itemId, updateItem.getAssociatedList()));
        }
        if (updateItem.getDueDate() != null) {
            final Date dueDate = Date.from(Instant.ofEpochMilli(updateItem.getDueDate()));
            commands.add(new AssignDueDate(itemId, dueDate));
        }
        if (updateItem.getRequiredTime() != null) {
            commands.add(new AssignRequiredTime(itemId, updateItem.getRequiredTime()));
        }
        if (updateItem.getTags() != null) {
            commands.add(new AssignTags(itemId, updateItem.getTags()));
        }
        return Optional.of(Collections.unmodifiableList(commands));
    }
}
