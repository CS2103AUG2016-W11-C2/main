package seedu.agendum.model.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * A read-only immutable interface for a Task in the ToDoList.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyTask {

    Name getName();
    boolean isCompleted();
    boolean isUpcoming();
    boolean isOverdue();
    boolean hasTime();
    Optional<LocalDateTime> getStartDateTime();
    Optional<LocalDateTime> getEndDateTime();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyTask other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName())) // state checks here onwards
                && (other.isCompleted() == this.isCompleted())
                && other.getStartDateTime().equals(this.getStartDateTime())
                && other.getEndDateTime().equals(this.getEndDateTime());
    }

    /**
     * Formats the task as text, showing all task details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm");
        builder.append(getName());
        if (getStartDateTime().isPresent()) {
            builder.append("\nStart time: ").append(this.getStartDateTime().get().format(formatter));
        }
        if (getEndDateTime().isPresent()) {
            builder.append("\nEnd time: ").append(this.getEndDateTime().get().format(formatter));
        }
        return builder.toString();
    }
}
