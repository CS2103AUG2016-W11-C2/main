package seedu.agendum.model.task;

import java.time.LocalDateTime;
import java.util.Optional;

public class ChildRecurringTask extends RecurringTask {

    private RecurringTask parent;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Name name;
    
    private boolean isLatestChild;

    public ChildRecurringTask(RecurringTask parent) {
        super(parent);
        this.name = parent.getName();
        this.startDateTime = parent.getStartDateTime().orElse(null);
        this.endDateTime = parent.getEndDateTime().orElse(null);
        this.parent = parent;
        parent.setChild(this);
    }
    
    public void setLatestChild() {
        isLatestChild = true;
    }
    
    public void unsetLatestChild() {
        isLatestChild = false;
    }
    
    @Override
    public void setStartDateTime(Optional<LocalDateTime> startDateTime) {
        this.startDateTime = startDateTime.orElse(null);
    }
    
    @Override
    public void setEndDateTime(Optional<LocalDateTime> endDateTime) {
        this.endDateTime = endDateTime.orElse(null);
    }
    
    @Override
    public Name getName() {
        return this.name;
    }
     
    @Override
    public RecurringTask getParent() {
        return this.parent;
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public Optional<LocalDateTime> getStartDateTime() {
        return Optional.ofNullable(startDateTime);
    }

    @Override
    public Optional<LocalDateTime> getEndDateTime() {
        return Optional.ofNullable(endDateTime);
    }
    
    @Override
    public boolean isChild() {
        return true;
    }
    
    @Override
    public boolean isLatestChild() {
        return isLatestChild;
    }
    
    @Override
    public String getPeriod() {
        return null;
    }
}
