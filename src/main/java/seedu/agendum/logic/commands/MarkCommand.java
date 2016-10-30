package seedu.agendum.logic.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Mark task(s) identified using their last displayed indices in the task listing.
 */
public class MarkCommand extends Command {

    // COMMAND_WORD, COMMAND_FORMAT, COMMAND_DESCRIPTION are for display in help window
    public static final String COMMAND_WORD = "mark";    
    public static final String COMMAND_FORMAT = "mark <id> <more-ids>";
    public static final String COMMAND_DESCRIPTION = "mark task(s) as completed";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " - "
            + COMMAND_DESCRIPTION + "\n"
            + COMMAND_FORMAT + "\n"
            + "(The id must be a positive number)\n"
            + "Example: " + COMMAND_WORD + " 1 3 5-6";

    public static final String MESSAGE_MARK_TASK_SUCCESS = "Marked Task(s): %1$s";

    public ArrayList<Integer> targetIndexes;

    public ArrayList<ReadOnlyTask> tasksToMark;

    //@@author A0133367E
    public MarkCommand(Set<Integer> targetIndexes) {
        this.targetIndexes = new ArrayList<>(targetIndexes);
        Collections.sort(this.targetIndexes);
        this.tasksToMark = new ArrayList<>();
    }

    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();

        if (isAnyIndexInvalid(lastShownList)) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
 
        for (int targetIndex: targetIndexes) {
            ReadOnlyTask taskToMark = lastShownList.get(targetIndex - 1);
            tasksToMark.add(taskToMark);
        }
        
        try {
            model.markTasks(tasksToMark);
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_MARK_TASK_SUCCESS, 
                CommandResult.tasksToString(tasksToMark, targetIndexes)));
    }

    private boolean isAnyIndexInvalid(UnmodifiableObservableList<ReadOnlyTask> lastShownList) {
        return targetIndexes.stream().anyMatch(index -> index > lastShownList.size());
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
