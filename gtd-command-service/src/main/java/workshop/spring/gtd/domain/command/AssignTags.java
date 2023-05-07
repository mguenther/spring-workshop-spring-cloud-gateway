package workshop.spring.gtd.domain.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AssignTags extends ItemCommand {

    private final List<String> tags;

    public AssignTags(final String itemId, final List<String> tags) {
        super(itemId);
        this.tags = new ArrayList<>(tags.size());
        this.tags.addAll(tags);
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "AssignTags{id=%s, tags=%s}", getItemId(), String.join(", ", getTags()));
    }
}
