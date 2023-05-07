package workshop.spring.gtd.domain.command;

import java.util.Locale;

public class AssignRequiredTime extends ItemCommand {

    private final Integer requiredTime;

    public AssignRequiredTime(final String itemId, final Integer requiredTime) {
        super(itemId);
        this.requiredTime = requiredTime;
    }

    public Integer getRequiredTime() {
        return requiredTime;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "AssignRequiredTime{id=%s, requiredTime=%s}", getItemId(), getRequiredTime());
    }
}
