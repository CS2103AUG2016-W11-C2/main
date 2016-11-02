package guitests;

import org.junit.Test;
import seedu.agendum.commons.core.Messages;
import seedu.agendum.testutil.TestTask;
import seedu.agendum.testutil.TypicalTestTasks;

public class FindCommandTest extends ToDoListGuiTest {

    @Test
    public void find_nonEmptyList() {
        assertFindResult("find Mark"); //no results
        assertFindResult("find Meier", TypicalTestTasks.benson, TypicalTestTasks.daniel); //multiple results

        //find after deleting one result
        commandBox.runCommand("delete 1");
        assertFindResult("find Meier", TypicalTestTasks.daniel);
    }

    @Test
    public void find_emptyList(){
        commandBox.runCommand("delete 1-7");
        assertFindResult("find Jean"); //no results
    }

    @Test
    public void find_invalidCommand_fail() {
        commandBox.runCommand("findgeorge");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
    
    @Test
    public void find_showMesssage() {
        commandBox.runCommand("find Meier");
        assertShowingMessage(Messages.MESSAGE_ESCAPE_HELP_WINDOW);
        assertFindResult("find Meier", TypicalTestTasks.benson, TypicalTestTasks.daniel); 
    }
    
    @Test
    public void find_showMessage_fail() {
        commandBox.runCommand("find2");
        assertShowingMessage(null);
    }
    
    @Test
    public void find_backToAllTasks_WithEscape() {
        assertFindResult("find Meier", TypicalTestTasks.benson, TypicalTestTasks.daniel);
        assertShowingMessage(Messages.MESSAGE_ESCAPE_HELP_WINDOW);
        mainGui.pressEscape();
        assertAllPanelsMatch(td.getTypicalTasks());
    }

    private void assertFindResult(String command, TestTask... expectedHits ) {
        commandBox.runCommand(command);
        assertListSize(expectedHits.length);
        assertResultMessage(String.format(Messages.MESSAGE_TASKS_LISTED_OVERVIEW, expectedHits.length));
        assertAllPanelsMatch(expectedHits);
    }
}
