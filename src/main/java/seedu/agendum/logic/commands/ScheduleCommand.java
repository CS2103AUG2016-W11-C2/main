package seedu.agendum.logic.commands;

import java.time.LocalDateTime;
import java.util.Optional;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.*;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;


/**
 * Reschedules a task in the to do list.
 */
public class ScheduleCommand extends Command {

    public static final String COMMAND_WORD = "schedule";
    public static String COMMAND_FORMAT = "schedule <name> \nschedule <name> by <deadline> \nschedule <name> from <start-time> to <end-time>";
    public static String COMMAND_DESCRIPTION = "update the time of a task";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Re-schedule an existing task. "
            + "Parameters: INDEX (must be a positive number) [new deadline/start/end time]\n"
            + "Example: " + COMMAND_WORD
            + " 2 from 7pm to 9pm";

    public static final String MESSAGE_SUCCESS = "Task #%1$s reschedule: %2$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists";

    public final int targetIndex;
    private final Optional<LocalDateTime> newStartDateTime;
    private final Optional<LocalDateTime> newEndDateTime;

    //temporary for message
    public ScheduleCommand() {
        this.targetIndex = 0;
        this.newStartDateTime = null;
        this.newEndDateTime = null;
    }

    /**
     * New task has no time specification
     */
    public ScheduleCommand(int targetIndex, Optional<LocalDateTime> startTime
            , Optional<LocalDateTime> endTime) {
        this.targetIndex = targetIndex;
        this.newStartDateTime = startTime;
        this.newEndDateTime = endTime;
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        ReadOnlyTask taskToSchedule = lastShownList.get(targetIndex - 1);

        try {
            Task updatedTask = new Task(taskToSchedule);
            updatedTask.setStartDateTime(newStartDateTime);
            updatedTask.setEndDateTime(newEndDateTime);
            model.updateTask(taskToSchedule, updatedTask);
            return new CommandResult(String.format(MESSAGE_SUCCESS, targetIndex, updatedTask));
        } catch (UniqueTaskList.DuplicateTaskException e) {
            return new CommandResult(MESSAGE_DUPLICATE_TASK);
        } catch (TaskNotFoundException e) {
            assert false : "The target task cannot be missing";
            return null;
        }
    }

    @Override
    public String getName() {
        return COMMAND_WORD;
    }
        
    @Override
    public String getFormat() {
        return COMMAND_FORMAT;
    }
        
    @Override
    public String getDescription() {
        return COMMAND_DESCRIPTION;
    }

}
