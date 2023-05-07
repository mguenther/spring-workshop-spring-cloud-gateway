package workshop.spring.gtd.domain.command;

import java.util.Locale;

public class MoveItemToList extends ItemCommand {

    private final String list;

    public MoveItemToList(final String itemId, final String list) {
        super(itemId);
        this.list = list;
    }

    public String getList() {
        return list;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "MoveItemToList{id=%s, list=%s}", getItemId(), getList());
    }
}
