package seedu.agendum.commons.events.ui;

import seedu.agendum.commons.events.BaseEvent;
import seedu.agendum.model.task.Task;

//@@author A0148031R
/**
 * Indicates a request to jump to the list of persons
 */
public class JumpToListRequestEvent extends BaseEvent {

    public final Task targetTask;
    public final boolean isMultipleTasks;

    public JumpToListRequestEvent(Task task, boolean isMultipleTasks) {
        this.targetTask = task;
        this.isMultipleTasks = isMultipleTasks;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}