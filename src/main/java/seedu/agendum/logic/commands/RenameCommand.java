package seedu.agendum.logic.commands;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.*;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;


/**
 * Renames a task in the task listing.
 */
public class RenameCommand extends Command {

    // COMMAND_WORD, COMMAND_FORMAT, COMMAND_DESCRIPTION are for display in help window
    public static final String COMMAND_WORD = "rename";
    public static final String COMMAND_FORMAT = "rename <id> <new-name>";
    public static final String COMMAND_DESCRIPTION = "update the name of a task";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " - "
            + COMMAND_DESCRIPTION + "\n"
            + COMMAND_FORMAT + "\n"
            + "Example: " + COMMAND_WORD + " 2 Watch Star Trek";

    public static final String MESSAGE_SUCCESS = "Task #%1$s renamed: %2$s";
    public static final String MESSAGE_DUPLICATE_TASK = "Hey, the task already exists";

    public int targetIndex = -1;
    public Name newTaskName = null;

    //@@author A0133367E
    /**
     * Constructor for rename command
     * @throws IllegalValueException only if the name is invalid
     */
    public RenameCommand(int targetIndex, String name)
            throws IllegalValueException {
        this.targetIndex = targetIndex;
        this.newTaskName = new Name(name);
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        ReadOnlyTask taskToRename = lastShownList.get(targetIndex - 1);

        try {
            Task renamedTask = new Task(taskToRename);
            renamedTask.setName(newTaskName);
            model.updateTask(taskToRename, renamedTask);
        } catch (UniqueTaskList.DuplicateTaskException e) {
            return new CommandResult(MESSAGE_DUPLICATE_TASK);
        } catch (TaskNotFoundException e) {
            assert false : "The target task cannot be missing";
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, targetIndex, newTaskName));

    }

    //@@author
    public static String getName() {
        return COMMAND_WORD;
    }

    public static String getFormat() {
        return COMMAND_FORMAT;
    }

    public static String getDescription() {
        return COMMAND_DESCRIPTION;
    }

}
