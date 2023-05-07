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
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class ItemsQueryResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository repository;

    @Autowired
    public ItemsQueryResource(final ItemRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "${api.get-items.description}",
            description = "${api.get-items.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response-codes.ok.description}"),
            @ApiResponse(responseCode = "500", description = "${api.response-codes.internalServerError.description}")
    })
    @GetMapping(path = "/api/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<List<Item>>> showAllItems() {

        log.info("Received a show all managed items request.");

        return repository
                .getItems()
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
