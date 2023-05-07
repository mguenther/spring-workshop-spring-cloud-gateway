package workshop.spring.gtd.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import workshop.spring.gtd.domain.Item;
import workshop.spring.gtd.domain.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class ItemQueryResource {

    private static final Logger log = LoggerFactory.getLogger(ItemQueryResource.class);

    private final ItemRepository repository;

    @Autowired
    public ItemQueryResource(final ItemRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "${api.get-item.description",
            description = "${api.get-item.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
            @ApiResponse(responseCode = "404", description = "${api.response-codes.notFound.description}"),
            @ApiResponse(responseCode = "500", description = "${api.response-codes.internalServerError.description}")
    })
    @GetMapping(path = "/api/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<Item>> showItem(@PathVariable("itemId") final String itemId) {

        log.info("Received a show item request for item with ID '{}'.", itemId);

        return repository
                .getItem(itemId)
                .thenApply(optionalItem -> optionalItem.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()))
                .exceptionally(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
