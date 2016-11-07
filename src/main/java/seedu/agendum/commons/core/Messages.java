package seedu.agendum.commons.core;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "We don't recognise this command";
    public static final String MESSAGE_UNKNOWN_COMMAND_WITH_SUGGESTION = "Did you mean '%1$s'?";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "We don't understand your input. Try\n%1$s";
    public static final String MESSAGE_INVALID_TASK_DISPLAYED_INDEX = "Hey, the task id given is invalid";
    public static final String MESSAGE_DUPLICATE_TASK = "Hey, the task already exists";
    public static final String MESSAGE_MISSING_TASK = "Something is wrong. Try again or restart Agendum?";
    public static final String MESSAGE_TASKS_LISTED_OVERVIEW = "%1$d tasks listed/found!";
    public static final String MESSAGE_ESCAPE_HELP_WINDOW = "Showing search results now, press ESC to go back and"
            + " view all tasks";
}
