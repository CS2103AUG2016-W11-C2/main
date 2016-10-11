package seedu.agendum.logic.commands;

import java.util.EmptyStackException;


/**
 * Undo the last command that mutate the to do list
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Previous command undone!";
    public static final String MESSAGE_FAILURE = "Nothing to undo!";

    public UndoCommand() {}


    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.restorePreviousToDoList();
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (EmptyStackException e) {
            return new CommandResult(MESSAGE_FAILURE);
        }
    }
}
