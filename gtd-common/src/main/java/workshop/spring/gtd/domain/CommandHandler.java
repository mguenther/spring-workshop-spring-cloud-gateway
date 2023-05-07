package workshop.spring.gtd.domain;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommandHandler<T> {

    CompletableFuture<Void> onCommand(T command);

    CompletableFuture<Void> onCommand(List<T> commands);
}
