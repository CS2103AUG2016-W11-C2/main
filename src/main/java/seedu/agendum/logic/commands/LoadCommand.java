package seedu.agendum.logic.commands;

import seedu.agendum.commons.util.StringUtil;
import seedu.agendum.commons.util.XmlUtil;

/**
 * Allow the user to specify a folder as the data storage location
 */
public class LoadCommand extends Command {
    
    public static final String COMMAND_WORD = "load";
    public static final String MESSAGE_SUCCESS = "Data successfully loaded from: %1$s";
    public static final String MESSAGE_LOCATION_INVALID = "The specified file path is invalid: %1$s";
    public static final String MESSAGE_FILE_WRONG_FORMAT = "The specified file is in the wrong format: %1$s";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specify a file to load from. \n"
            + "Parameters: FILE_PATH\n" 
            + "Example: " + COMMAND_WORD 
            + "agendum/todolist.xml";
    private String loadLocation;

    public LoadCommand(String location) {
        loadLocation = location.trim();
    }

    @Override
    public CommandResult execute() {
        assert loadLocation != null;

        if(!isLocationValid()) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(String.format(MESSAGE_LOCATION_INVALID, loadLocation));
        }
        
        if(!isFileCorrectFormat()) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(String.format(MESSAGE_FILE_WRONG_FORMAT, loadLocation));            
        }

        model.loadFromLocation(loadLocation);
        return new CommandResult(String.format(MESSAGE_SUCCESS, loadLocation));
    }
    
    private boolean isFileCorrectFormat() {
        return XmlUtil.isFileCorrectFormat(loadLocation);
    }

    private boolean isLocationValid() {
        return StringUtil.isValidFilePath(loadLocation);
    }
    

}
