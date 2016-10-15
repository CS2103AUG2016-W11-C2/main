package seedu.agendum.logic.commands;

import seedu.agendum.commons.core.Config;
import seedu.agendum.commons.util.FileUtil;
import seedu.agendum.commons.util.StringUtil;

/**
 * Allow the user to specify a folder as the data storage location
 */
public class StoreCommand extends Command {
    
    public static final String COMMAND_WORD = "store";
    public static final String MESSAGE_SUCCESS = "New save location: %1$s";
    public static final String MESSAGE_LOCATION_INVALID = "The specified file path is invalid.";
    public static final String MESSAGE_LOCATION_DEFAULT = "Save location set to default: %1$s";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specify a save location. \n"
            + "Parameters: FILE_PATH\n" 
            + "Example: " + COMMAND_WORD 
            + "C:/agendum";
    private String newSaveLocation;

    public StoreCommand(String location) {
        newSaveLocation = location.trim();
    }

    @Override
    public CommandResult execute() {
        assert newSaveLocation != null;
        
        if(newSaveLocation.equalsIgnoreCase("default")) {
            String defaultLocation = Config.DEFAULT_SAVE_LOCATION;
            model.changeSaveLocation(defaultLocation);
            return new CommandResult(String.format(MESSAGE_LOCATION_DEFAULT, defaultLocation));
        }

        if(!isNewSaveLocationValid()) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(MESSAGE_LOCATION_INVALID);
        }

        model.changeSaveLocation(newSaveLocation);
        return new CommandResult(String.format(MESSAGE_SUCCESS, newSaveLocation));
    }
    
    private boolean isNewSaveLocationValid() {
        boolean stringValid = StringUtil.isValidFilePath(newSaveLocation);
        if(!stringValid) {// Don't do the more expensive check if this one fails
            return false;
        }
        boolean locationValid = FileUtil.isPathAvailable(newSaveLocation);
        
        return stringValid && locationValid;
    }
    
}
