package workshop.spring.gtd.domain;

import workshop.spring.gtd.event.DueDateAssignedEvent;
import workshop.spring.gtd.event.ItemClosedEvent;
import workshop.spring.gtd.event.ItemCreatedEvent;
import workshop.spring.gtd.event.ItemEvent;
import workshop.spring.gtd.event.ItemMovedToListEvent;
import workshop.spring.gtd.event.RequiredTimeAssignedEvent;
import workshop.spring.gtd.event.TagAssignedEvent;
import workshop.spring.gtd.event.TagRemovedEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Item {

    private String id;

    private String description;

    private Integer requiredTime;

    private Date dueDate;

    private List<String> tags;

    private String associatedList;

    private Boolean done;

    private Date lastModified;

    public Item(final ItemCreatedEvent event) {
        this.id = event.getItemId();
        this.description = event.getDescription();
        this.tags = new ArrayList<>();
        this.requiredTime = 0;
        this.done = false;
        this.lastModified = now();
    }

    public String getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredTime() {
        return requiredTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public String getAssociatedList() {
        return associatedList;
    }

    public void project(final ItemEvent event) {
        if (event instanceof DueDateAssignedEvent) {
            var ev = (DueDateAssignedEvent) event;
            project(ev);
        } else if (event instanceof RequiredTimeAssignedEvent) {
            var ev = (RequiredTimeAssignedEvent) event;
            project(ev);
        } else if (event instanceof TagAssignedEvent) {
            var ev = (TagAssignedEvent) event;
            project(ev);
        } else if (event instanceof TagRemovedEvent) {
            var ev = (TagRemovedEvent) event;
            project(ev);
        } else if (event instanceof ItemClosedEvent) {
            var ev = (ItemClosedEvent) event;
            project(ev);
        } else if (event instanceof ItemMovedToListEvent) {
            var ev = (ItemMovedToListEvent) event;
            project(ev);
        } else {
            throw new IllegalStateException("Unrecognized event: " + event.toString());
        }
    }

    private void project(final DueDateAssignedEvent event) {
        this.dueDate = Date.from(Instant.ofEpochMilli(event.getDueDate()));
        this.lastModified = now();
    }

    private void project(final RequiredTimeAssignedEvent event) {
        this.requiredTime = event.getRequiredTime();
        this.lastModified = now();
    }

    private void project(final TagAssignedEvent event) {
        synchronized (this) {
            if (!tags.contains(event.getTag())) {
                tags.add(event.getTag());
            }
        }
        this.lastModified = now();
    }

    private void project(final TagRemovedEvent event) {
        synchronized (this) {
            tags.remove(event.getTag());
        }
        this.lastModified = now();
    }

    private void project(final ItemClosedEvent __) {
        this.done = true;
        this.lastModified = now();
    }

    private void project(final ItemMovedToListEvent event) {
        this.associatedList = event.getList();
        this.lastModified = now();
    }

    private Date now() {
        return Date.from(Instant.now(Clock.systemUTC()));
    }
}
