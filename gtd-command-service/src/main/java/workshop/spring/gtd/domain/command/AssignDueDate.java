package workshop.spring.gtd.domain.command;

import java.util.Date;
import java.util.Locale;

public class AssignDueDate extends ItemCommand {

    private final Date dueDate;

    public AssignDueDate(final String itemId, final Date dueDate) {
        super(itemId);
        this.dueDate = dueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "AssignDueDate{id=%s, dueDate=%s}", getItemId(), getDueDate());
    }
}
