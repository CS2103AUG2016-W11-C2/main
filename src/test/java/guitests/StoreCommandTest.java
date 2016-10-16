package guitests;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import seedu.agendum.commons.core.Config;
import seedu.agendum.commons.exceptions.FileDeletionException;
import seedu.agendum.commons.util.FileUtil;
import seedu.agendum.logic.commands.StoreCommand;

public class StoreCommandTest extends ToDoListGuiTest {

    @Test
    public void store() throws IOException, FileDeletionException {
        String testLocation = "data/test.xml";
        String badLocation = "test/.xml";
        
        //save to a valid directory
        commandBox.runCommand("store " + testLocation);
        assertResultMessage(String.format(StoreCommand.MESSAGE_SUCCESS, testLocation));

        //save to default directory
        commandBox.runCommand("store default");
        assertResultMessage(String.format(StoreCommand.MESSAGE_LOCATION_DEFAULT, Config.DEFAULT_SAVE_LOCATION));
                
        //invalid directory
        commandBox.runCommand("store " + badLocation);
        assertResultMessage(StoreCommand.MESSAGE_LOCATION_INVALID);
        
        //file exists
        FileUtil.createIfMissing(new File(testLocation));
        commandBox.runCommand("store " + testLocation);
        assertResultMessage(StoreCommand.MESSAGE_FILE_EXISTS);
        FileUtil.deleteFile(testLocation);
        
    }
}
