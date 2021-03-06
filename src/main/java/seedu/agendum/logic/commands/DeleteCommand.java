package seedu.agendum.logic.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;

//@@author A0133367E
/**
 * Deletes task(s) identified using their last displayed indices from the task listing.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_FORMAT = "delete <id> <more ids>";
    public static final String COMMAND_DESCRIPTION = "delete task(s) from Agendum";
    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Deleted Task(s): %1$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " - "
            + COMMAND_DESCRIPTION + "\n"
            + COMMAND_FORMAT + "\n"
            + "(The id must be a positive number)\n"
            + "Example: " + COMMAND_WORD + " 7 10-11";

    private ArrayList<Integer> targetIndexes;
    private ArrayList<ReadOnlyTask> tasksToDelete;


    public DeleteCommand(Set<Integer> targetIndexes) {
        this.targetIndexes = new ArrayList<Integer>(targetIndexes);
        Collections.sort(this.targetIndexes);
        this.tasksToDelete = new ArrayList<ReadOnlyTask>();
    }

    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();

        if (isAnyIndexInvalid(lastShownList)) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
 
        for (int targetIndex: targetIndexes) {
            ReadOnlyTask taskToDelete = lastShownList.get(targetIndex - 1);
            tasksToDelete.add(taskToDelete);
        }

        try {
            model.deleteTasks(tasksToDelete);
        } catch (TaskNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_MISSING_TASK);
        }

        return new CommandResult(String.format(MESSAGE_DELETE_TASK_SUCCESS,
                CommandResult.tasksToString(tasksToDelete, targetIndexes)));
    }

    private boolean isAnyIndexInvalid(UnmodifiableObservableList<ReadOnlyTask> lastShownList) {
        return targetIndexes.stream().anyMatch(index -> index > lastShownList.size());
    }

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
