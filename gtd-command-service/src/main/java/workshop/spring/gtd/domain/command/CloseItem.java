package workshop.spring.gtd.domain.command;

import java.util.Locale;

public class CloseItem extends ItemCommand {

    public CloseItem(final String itemId) {
        super(itemId);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "ConcludeItem{id=%s}", getItemId());
    }
}
