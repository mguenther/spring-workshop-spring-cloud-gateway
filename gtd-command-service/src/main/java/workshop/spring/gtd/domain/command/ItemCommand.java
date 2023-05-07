package workshop.spring.gtd.domain.command;

abstract public class ItemCommand {

    private final String itemId;

    public ItemCommand(final String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}
