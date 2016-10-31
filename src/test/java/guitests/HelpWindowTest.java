package guitests;

import guitests.guihandles.HelpWindowHandle;
import guitests.guihandles.MainGuiHandle;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

//@@author A0148031R
public class HelpWindowTest extends ToDoListGuiTest {

    @Test
    public void openHelpWindow() {

        assertHelpWindowOpen(mainMenu.openHelpWindowUsingAccelerator());

        assertHelpWindowOpen(commandBox.runHelpCommand());

    }

    @Test
    public void closeHelpWindow() {
        assertHelpWindowClose(mainMenu.closeHelpWindowUsingAccelerator());
    }

    private void assertHelpWindowClose(MainGuiHandle mainGuiHandle) {
        commandBox.runHelpCommand();
        assertTrue(mainGuiHandle.isWindowClose());
    }
    
    private void assertHelpWindowOpen(HelpWindowHandle helpWindowHandle) {
        assertTrue(helpWindowHandle.isWindowOpen());
        helpWindowHandle.closeWindow();
    }
}
