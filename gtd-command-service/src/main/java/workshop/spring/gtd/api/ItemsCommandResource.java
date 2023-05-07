package workshop.spring.gtd.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import workshop.spring.gtd.domain.CommandHandler;
import workshop.spring.gtd.domain.command.CreateItem;
import workshop.spring.gtd.domain.command.ItemCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RestController
public class ItemsCommandResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CommandHandler<ItemCommand> handler;

    @Autowired
    public ItemsCommandResource(final CommandHandler<ItemCommand> handler) {
        this.handler = handler;
    }

    @Operation(
            summary = "${api.create-item.description}",
            description = "${api.create-item.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "${api.response-codes.created.description}"),
            @ApiResponse(responseCode = "500", description = "${api.response-codes.internalServerError.description}")
    })
    @PostMapping(path = "/api/items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<Object>> createItem(@RequestBody CreateItemRequest payload) {

        log.info("Received a create item request with data '{}'.", payload);

        final var commands = commandsFor(payload);

        return handler
                .onCommand(commands)
                .thenApply(__ -> ResponseEntity.created(itemUri(commands.getItemId())).build())
                .exceptionally(e -> {
                    log.warn("Caught an exception at the service boundary.", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    private ItemCommand commandsFor(final CreateItemRequest createItem) {
        return new CreateItem(createItem.getDescription());
    }

    private URI itemUri(final String itemId) {
        return URI.create("/items/" + itemId);
    }
}
