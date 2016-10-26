package seedu.agendum.logic.commands;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.logic.CommandLibrary;

/**
 * Create an alias for a reserved command keyword
 */
public class AliasCommand extends Command {

    // COMMAND_WORD, COMMAND_FORMAT, COMMAND_DESCRIPTION are for display in help window
    public static final String COMMAND_WORD = "alias";
    public static String COMMAND_FORMAT = "alias <original-command> <your-command>";
    public static String COMMAND_DESCRIPTION = "specify your own shorthand command";
    public static final String MESSAGE_SUCCESS = "New alias <%1$s> created for <%2$s>";
    public static final String MESSAGE_FAILURE_DUPLICATE = "<%1$s> is already an alias for <%2$s>";
    public static final String MESSAGE_FAILURE_NON_ORIGINAL_COMMAND = 
            "We do not recognise <%1$s> as an Agendum Command";
    public static final Object MESSAGE_USAGE = COMMAND_WORD 
            + ": Creates an alias for a reserved command word \n"
            + "Parameters: ORIGINAL-COMMAND-WORD YOUR-SHORTHAND-COMMAND\n"
            + "Example: " + COMMAND_WORD
            + " mark m";

    private static final CommandLibrary commandLibrary = CommandLibrary.getInstance();

    private String aliasValue = null;
    private String aliasKey = null;

    public AliasCommand() {}
    
    public AliasCommand(String aliasKey, String aliasValue) {
        this.aliasKey = aliasKey;
        this.aliasValue = aliasValue;
    }

    @Override
    public CommandResult execute() {
        if (!commandLibrary.isValidAliasValue(aliasValue)) {
            return new CommandResult(String.format(
                    MESSAGE_FAILURE_NON_ORIGINAL_COMMAND, aliasValue));
        }

        if (commandLibrary.isExistingAliasKey(aliasKey)) {
            String associatedValue = commandLibrary.getAliasedValue(aliasKey);
            return new CommandResult(String.format(
                    MESSAGE_FAILURE_DUPLICATE, aliasKey, associatedValue));
        }
        
        commandLibrary.addNewAlias(aliasKey, aliasValue);
        return new CommandResult(String.format(MESSAGE_SUCCESS, aliasKey, aliasValue));
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
