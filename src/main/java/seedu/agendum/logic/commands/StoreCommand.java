package seedu.agendum.logic.commands;

import java.io.File;

import seedu.agendum.commons.core.Config;
import seedu.agendum.commons.util.FileUtil;
import seedu.agendum.commons.util.StringUtil;

/**
 * Allow the user to specify a folder as the data storage location
 */
public class StoreCommand extends Command {
    
    public static final String COMMAND_WORD = "store";
    public static final String MESSAGE_SUCCESS = "New save location: %1$s";
    public static final String MESSAGE_LOCATION_INVALID = "The specified location is invalid.";
    public static final String MESSAGE_LOCATION_DEFAULT = "Save location set to default: %1$s";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specify a save location. \n"
            + "Parameters: FILE_PATH\n" 
            + "Example: " + COMMAND_WORD 
            + "agendum/todolist.xml";
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

        if(!isLocationValid()) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(MESSAGE_LOCATION_INVALID);
        }

        model.changeSaveLocation(newSaveLocation);
        return new CommandResult(String.format(MESSAGE_SUCCESS, newSaveLocation));
    }
    
    private boolean isLocationValid() {
        boolean isValidFilePath = StringUtil.isValidFilePath(newSaveLocation);
        if(!isValidFilePath) {// Don't do the more expensive check if this one fails
            return false;
        }
        boolean isPathAvailable = FileUtil.isPathAvailable(newSaveLocation);
        
        return isValidFilePath && isPathAvailable;
    }
    
    private boolean isFileExists() {
        return FileUtil.isFileExists(new File(newSaveLocation));
    }
    
}
