package guitests.guihandles;

import guitests.GuiRobot;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import seedu.agendum.TestApp;

import java.util.Arrays;

/**
 * Provides a handle to the main menu of the app.
 */
public class MainMenuHandle extends GuiHandle {
    public MainMenuHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public GuiHandle clickOn(String... menuText) {
        Arrays.stream(menuText).forEach((menuItem) -> guiRobot.clickOn(menuItem));
        return this;
    }

    public HelpWindowHandle openHelpWindowUsingMenu() {
        clickOn("Help", "⌥H");
        return new HelpWindowHandle(guiRobot, primaryStage);
    }

    public HelpWindowHandle openHelpWindowUsingAccelerator() {
        useAccelerator();
        return new HelpWindowHandle(guiRobot, primaryStage);
    }

    private void useAccelerator() {
        guiRobot.push(new KeyCodeCombination(KeyCode.H, KeyCombination.ALT_DOWN));
        guiRobot.sleep(500);
    }
}
