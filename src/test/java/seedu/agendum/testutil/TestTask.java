package seedu.agendum.testutil;

import java.time.LocalDateTime;
import java.util.Optional;

import seedu.agendum.model.task.*;

/**
 * A mutable task object. For testing only.
 */
public class TestTask implements ReadOnlyTask {

    private static final int UPCOMING_DAYS_THRESHOLD = 7;

    private Name name;
    private boolean isCompleted;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime lastUpdatedTime;

    public TestTask() {
        isCompleted = false;
        startDateTime = null;
        endDateTime = null;
        setLastUpdatedTime();
    }

    /**
     * Copy constructor.
     */
    public TestTask(TestTask other) {
        this.name = other.name;
        this.isCompleted = other.isCompleted;
        this.startDateTime = other.startDateTime;
        this.endDateTime = other.endDateTime;
        setLastUpdatedTime();
    }

    public void setName(Name name) {
        this.name = name;
        setLastUpdatedTime();
    }
    
    public void markAsCompleted() {
        this.isCompleted = true;
        setLastUpdatedTime();
    }

    public void markAsUncompleted() {
        this.isCompleted = false;
        setLastUpdatedTime();
    }

    public void setStartDateTime(Optional<LocalDateTime> startDateTime) {
        this.startDateTime = startDateTime.orElse(null);
        setLastUpdatedTime();
    }
    
    public void setEndDateTime(Optional<LocalDateTime> endDateTime) {
        this.endDateTime = endDateTime.orElse(null);
        setLastUpdatedTime();
    }

    private void setLastUpdatedTime() {
        this.lastUpdatedTime = LocalDateTime.now();
    }

    @Override
    public Name getName() {
        return name;
    }
    
    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isUpcoming() {
        return  !isCompleted() && hasTime() && getTaskTime().isBefore(
                LocalDateTime.now().plusDays(UPCOMING_DAYS_THRESHOLD));
    }

    @Override
    public boolean isOverdue() {
        return !isCompleted() && hasTime() && getTaskTime().isBefore(LocalDateTime.now());
    }

    @Override
    public boolean hasTime() {
        return (getStartDateTime().isPresent() || getEndDateTime().isPresent());
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
    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    /**
     * Pre-condition: Task has a start or end time
     * Return the (earlier) time associated with the task
     */
    private LocalDateTime getTaskTime() {
        assert hasTime();
        return getStartDateTime().orElse(getEndDateTime().get());
    }

    @Override
    public String toString() {
        return getAsText();
    }

    public String getAddCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append("add " + this.getName().fullName + " ");
        return sb.toString();
    }

}
