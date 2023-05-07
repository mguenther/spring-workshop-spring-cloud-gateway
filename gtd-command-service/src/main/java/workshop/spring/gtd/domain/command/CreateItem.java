package workshop.spring.gtd.domain.command;

import java.util.Locale;
import java.util.UUID;

public class CreateItem extends ItemCommand {

    private final String description;

    public CreateItem(final String description) {
        super(generateItemId());
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    private static String generateItemId() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "CreateItem{id=%s, description=%s}", getItemId(), getDescription());
    }
}
