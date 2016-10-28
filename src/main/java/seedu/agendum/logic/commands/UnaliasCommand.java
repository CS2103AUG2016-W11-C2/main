package seedu.agendum.logic.commands;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.model.Model;

/**
 * Create an alias for a reserved command keyword
 */
public class UnaliasCommand extends Command {

    // COMMAND_WORD, COMMAND_FORMAT, COMMAND_DESCRIPTION are for display in help window
    public static final String COMMAND_WORD = "unalias";
    public static String COMMAND_FORMAT = "unalias <your-command>";
    public static String COMMAND_DESCRIPTION = "remove your own shorthand command";
    public static final String MESSAGE_SUCCESS = "Alias <%1$s> is gone";
    public static final String MESSAGE_FAILURE_NO_ALIAS_KEY = 
            "The alias <%1$s> does not exist";
    public static final Object MESSAGE_USAGE = COMMAND_WORD 
            + ": Unalias a shorthand command defined \n"
            + "Parameters: SHORTHAND-COMMAND\n"
            + "Example: " + COMMAND_WORD
            + " m (if m is an alias for mark)";

    private String aliasKey;
    private CommandLibrary commandLibrary;
    
    public UnaliasCommand(String aliasKey) {
        this.aliasKey = aliasKey;
    }

    public void setData(Model model, CommandLibrary commandLibrary) {
        this.model = model;
        this.commandLibrary = commandLibrary;
    }

    @Override
    public CommandResult execute() {
        if (!commandLibrary.isExistingAliasKey(aliasKey)) {
            return new CommandResult(String.format(
                    MESSAGE_FAILURE_NO_ALIAS_KEY, aliasKey));
        }
        
        commandLibrary.removeExistingAlias(aliasKey);
        return new CommandResult(String.format(MESSAGE_SUCCESS, aliasKey));
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
