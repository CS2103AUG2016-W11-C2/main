package seedu.agendum.model.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import seedu.agendum.logic.parser.DateTimeParser;

public class RecurringTask extends Task {

    private String period;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Name name;
    
    private final String BEFORE = " before ";
    private final String FROM = " from ";
    
    private ArrayList<ChildRecurringTask> children = new ArrayList<ChildRecurringTask>();
    
    public RecurringTask(Name name, Optional<LocalDateTime> startDateTime, 
            Optional<LocalDateTime> endDateTime, String period) {
        super(name, startDateTime, endDateTime);
        this.name = name;
        this.startDateTime = startDateTime.orElse(null);
        this.endDateTime = endDateTime.orElse(null);
        this.period = period;
    }
    
    public RecurringTask(Name name, Optional<LocalDateTime> endDateTime, String period) {
        super(name, endDateTime);
        this.name = name;
        this.startDateTime = null;
        this.endDateTime = endDateTime.orElse(null);
        this.period = period;
    }
    
    public RecurringTask(ReadOnlyTask source) {
        super(source);
        this.name = source.getName();
        this.startDateTime = source.getStartDateTime().orElse(null);
        this.endDateTime = source.getEndDateTime().orElse(null);
        this.period = source.getPeriod();
    }
    
    public RecurringTask(RecurringTask self) {
        super(self);
    }
    
    public void setChild(ChildRecurringTask child) {
        child.setLatestChild();
        if(this.children.size() >= 1) {
            this.children.get(this.children.size() - 1).unsetLatestChild();
        }
        this.children.add(child);
    }
    
    public void setNextDateTime() {
        if(this.startDateTime != null) {
            this.startDateTime = DateTimeParser.parseString(period + FROM + this.startDateTime.toString()).get();
        }
        this.endDateTime = DateTimeParser.parseString(period + FROM + this.endDateTime.toString()).get();
    }
    
    public void setPreviousDateTime() {
        if(this.startDateTime != null) {
            this.startDateTime = DateTimeParser.parseString(period + BEFORE + this.startDateTime.toString()).get();
        } 
        this.endDateTime = DateTimeParser.parseString(period + BEFORE + this.endDateTime.toString()).get();
        if(this.children.size() >= 2) {
            this.children.get(this.children.size() - 2).setLatestChild();
        }
        this.children.remove(this.children.size() - 1);
    }
    
    @Override
    public void setName(Name name) {
        this.name = name;
    }
    
    @Override
    public Name getName() {
        return this.name;
    }
    
    @Override
    public Optional<LocalDateTime> getStartDateTime() {
        return Optional.ofNullable(this.startDateTime);
    }

    @Override
    public Optional<LocalDateTime> getEndDateTime() {
        return Optional.ofNullable(this.endDateTime);
    }

    @Override
    public ChildRecurringTask getChild() {
        ChildRecurringTask child = new ChildRecurringTask(this);
        this.setNextDateTime();
        return child;
    }
    
    @Override
    public RecurringTask getParent() {
        return null;
    }
    
    @Override
    public boolean isRecurring() {
        return true;
    }
    
    @Override
    public boolean isCompleted() {
        return false;
    }
    
    @Override
    public void markAsCompleted() {
        return;
    }
    
    @Override
    public boolean isLatestChild() {
        return false;
    }
    
    @Override
    public String getPeriod() {
        return period;
    }
}
