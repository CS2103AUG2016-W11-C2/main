package seedu.agendum.ui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.agendum.commons.core.Config;
import seedu.agendum.commons.core.GuiSettings;
import seedu.agendum.commons.events.ui.ExitAppRequestEvent;
import seedu.agendum.logic.Logic;
import seedu.agendum.model.UserPrefs;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart {

    private static final String ICON = "/images/agendum_icon.png";
    private static final String FXML = "MainWindow.fxml";
    public static final String LIST_COMMAND = "list";

    private Logic logic;
    
    // Independent Ui parts residing in this Ui container
    private TasksPanel upcomingTasksPanel;
    private TasksPanel completedTasksPanel;
    private TasksPanel floatingTasksPanel;
    private ResultPopUp resultPopUp;
    private StatusBarFooter statusBarFooter;
    private CommandBox commandBox;
    private Config config;
    private UserPrefs userPrefs;

    // Handles to elements of this Ui container
    private VBox rootLayout;
    private Scene scene;
    private Stage helpWindowStage = null;

    @FXML
    private AnchorPane browserPlaceholder;

    @FXML
    private AnchorPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private AnchorPane upcomingTasksPlaceHolder;
    
    @FXML
    private AnchorPane completedTasksPlaceHolder;
    
    @FXML
    private AnchorPane floatingTasksPlaceHolder;
    
    @FXML
    private AnchorPane statusbarPlaceholder;
    
    @FXML
    private StackPane messagePlaceHolder;

    public MainWindow() {
        super();
    }

    @Override
    public void setNode(Node node) {
        rootLayout = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    public static MainWindow load(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {
        MainWindow mainWindow = UiPartLoader.loadUiPart(primaryStage, new MainWindow());
        mainWindow.configure(config.getAppTitle(), config.getToDoListName(), config, prefs, logic);
        return mainWindow;
    }

    //@@author A0148031R
    private void configure(String appTitle, String toDoListName, Config config, UserPrefs prefs,
                           Logic logic) {

        //Set dependencies
        this.logic = logic;
        this.config = config;
        this.userPrefs = prefs;

        //Configure the UI
        setTitle(appTitle);
        setIcon(ICON);
        setWindowDefaultSize(prefs);
        scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        setAccelerators();
        configureEscape();
        configureHelpWindowToggle();
    }

    /**
     * Set shortcut key for help menu item
     */
    private void setAccelerators() {
        helpMenuItem.setAccelerator(KeyCombination.valueOf("F5"));
    }
    
    /**
     * Set shortcut key to switch between help window and main window
     */
    private void configureHelpWindowToggle() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            KeyCombination toggleHelpWindow = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
            @Override
            public void handle(KeyEvent evt) {
                if(toggleHelpWindow.match(evt) && helpWindowStage != null) {
                    if(helpWindowStage.isFocused()) {
                        primaryStage.requestFocus();
                    } else {
                        helpWindowStage.requestFocus();
                    }
                }
            }
        });
    }
    
    /**
     * Set shortcut key to quickly switch back to main list after using find
     * command or showing help page
     */
    private void configureEscape() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent evt) {
                if (evt.getCode().equals(KeyCode.ESCAPE) && messagePlaceHolder.getChildren().size() != 0) {
                    messagePlaceHolder.getChildren().remove(0);
                    messagePlaceHolder.setMaxHeight(0);
                    logic.execute(LIST_COMMAND);
                }
            }
        });
    }


    /**
     * Loads the ui elements
     */
    void fillInnerParts() {
        upcomingTasksPanel = UpcomingTasksPanel.load(primaryStage, getUpcomingTasksPlaceHolder(), 
                logic.getFilteredTaskList(), new UpcomingTasksPanel());
        completedTasksPanel = CompletedTasksPanel.load(primaryStage, getCompletedTasksPlaceHolder(), 
                logic.getFilteredTaskList(), new CompletedTasksPanel());
        floatingTasksPanel = FloatingTasksPanel.load(primaryStage, getFloatingTasksPlaceHolder(), 
                logic.getFilteredTaskList(), new FloatingTasksPanel());
        resultPopUp = ResultPopUp.load(primaryStage);
        statusBarFooter = StatusBarFooter.load(primaryStage, getStatusbarPlaceholder(), config.getToDoListFilePath());
        commandBox = CommandBox.load(primaryStage, getCommandBoxPlaceholder(), messagePlaceHolder, resultPopUp, logic);
    }

    private AnchorPane getCommandBoxPlaceholder() {
        return commandBoxPlaceholder;
    }
    
    public StackPane getMessagePlaceHolder() {
        return messagePlaceHolder;
    }

    private AnchorPane getStatusbarPlaceholder() {
        return statusbarPlaceholder;
    }
    
    public AnchorPane getUpcomingTasksPlaceHolder() {
        return upcomingTasksPlaceHolder;
    }
    
    public AnchorPane getCompletedTasksPlaceHolder() {
        return completedTasksPlaceHolder;
    }
    
    public AnchorPane getFloatingTasksPlaceHolder() {
        return floatingTasksPlaceHolder;
    }
    
    public TasksPanel getUpcomingTasksPanel() {
        return (UpcomingTasksPanel)this.upcomingTasksPanel;
    }
    
    public TasksPanel getCompletedTasksPanel() {
        return (CompletedTasksPanel)this.completedTasksPanel;
    }
    
    public TasksPanel getFloatingasksPanel() {
        return (FloatingTasksPanel)this.floatingTasksPanel;
    }
    
    /**
     * Sets the default size based on user preferences.
     */
    protected void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());

        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    public GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    //@@author A0148031R
    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = HelpWindow.load(primaryStage);
        if(helpWindow != null) {
            this.helpWindowStage = helpWindow.getStage();
            helpWindow.show();
        }
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    public void hide() {
        primaryStage.hide();
    }

    public void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }
}
