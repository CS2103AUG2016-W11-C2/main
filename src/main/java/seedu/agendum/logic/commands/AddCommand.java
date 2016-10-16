package seedu.agendum.logic.commands;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.tag.Tag;
import seedu.agendum.model.tag.UniqueTagList;
import seedu.agendum.model.task.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Adds a task to the to do list.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    public static String COMMAND_FORMAT = "add <name> \nadd <name> by <deadline> \nadd <name> from <start-time> to <end-time>";
    public static String COMMAND_DESCRIPTION = "adds a task to Agendum";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task with no time and date. \n"
            + "Parameters: NAME [t/TAG]...\n"
            + "Example: " + COMMAND_WORD
            + " Watch Star Wars t/movies";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists";

    private Task toAdd = null;

    public AddCommand() {}
    
    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name, Set<String> tags)
            throws IllegalValueException {
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        this.toAdd = new Task(
                new Name(name),
                new UniqueTagList(tagSet)
        );
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.addTask(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (UniqueTaskList.DuplicateTaskException e) {
            return new CommandResult(MESSAGE_DUPLICATE_TASK);
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
