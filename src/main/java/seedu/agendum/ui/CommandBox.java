package seedu.agendum.ui;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seedu.agendum.commons.events.ui.CloseHelpWindowRequestEvent;
import seedu.agendum.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.agendum.logic.Logic;
import seedu.agendum.logic.commands.*;
import seedu.agendum.logic.parser.EditDistanceCalculator;
import seedu.agendum.commons.util.FxViewUtil;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.commons.core.Messages;

import java.util.Optional;
import java.util.logging.Logger;

//@@author A0148031R
public class CommandBox extends UiPart {
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";
    private static final String FIND_COMMAND = "find ";
    private static final String HELP_COMMAND = "help";
    private static final String RESULT_FEEDBACK = "Result: ";
    private static final String ERROR = "error";

    private AnchorPane placeHolderPane;
    private AnchorPane commandPane;
    private StackPane messagePlaceHolder;
    private ResultPopUp resultPopUp;
    private static CommandBoxHistory commandBoxHistory;

    private Logic logic;

    @FXML
    private TextField commandTextField;

    public static CommandBox load(Stage primaryStage, AnchorPane commandBoxPlaceholder, StackPane messagePlaceHolder, 
            ResultPopUp resultPopUp, Logic logic) {
        CommandBox commandBox = UiPartLoader.loadUiPart(primaryStage, commandBoxPlaceholder, new CommandBox());
        commandBox.configure(resultPopUp, messagePlaceHolder, logic);
        commandBox.addToPlaceholder();
        commandBoxHistory = CommandBoxHistory.getInstance();
        return commandBox;
    }

    public void configure(ResultPopUp resultPopUp, StackPane messagePlaceHolder, Logic logic) {
        this.resultPopUp = resultPopUp;
        this.messagePlaceHolder = messagePlaceHolder;
        this.logic = logic;
        registerAsAnEventHandler(this);
        registerArrowKeyEventFilter();
        registerTabKeyEventFilter();
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        FxViewUtil.applyAnchorBoundaryParameters(commandPane, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(commandTextField, 0.0, 0.0, 0.0, 0.0);
        placeHolderPane.getChildren().add(commandTextField);
    }

    @Override
    public void setNode(Node node) {
        commandPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    /**
     * Executes the command and saves this command to history if comamnd input
     * is changed
     */
    @FXML
    private void handleCommandInputChanged() {
        //Take a copy of the command text
        commandBoxHistory.saveNewCommand(commandTextField.getText());
        String previousCommandTest = commandBoxHistory.getLastCommand();
        if(previousCommandTest.toLowerCase().trim().startsWith(FIND_COMMAND) && 
                previousCommandTest.toLowerCase().trim().length() > FIND_COMMAND.length()) {
            postMessage(Messages.MESSAGE_ESCAPE_HELP_WINDOW);
        } else {
            raise(new CloseHelpWindowRequestEvent());
        }

        /* We assume the command is correct. If it is incorrect, the command box will be changed accordingly
         * in the event handling code {@link #handleIncorrectCommandAttempted}
         */

        setStyleToIndicateCorrectCommand();
        CommandResult mostRecentResult = logic.execute(previousCommandTest);
        if(!previousCommandTest.toLowerCase().equals(HELP_COMMAND)) {
            resultPopUp.postMessage(mostRecentResult.feedbackToUser);
        }
        logger.info(RESULT_FEEDBACK + mostRecentResult.feedbackToUser);
    }
    
    private void postMessage(String message) {
        this.messagePlaceHolder.getChildren().clear();
        raise(new CloseHelpWindowRequestEvent());

        Label label = new Label(message);
        label.setTextFill(Color.web("#ffffff"));
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setPadding(new Insets(0, 10, 0, 10));
        this.messagePlaceHolder.setAlignment(Pos.CENTER_LEFT);
        this.messagePlaceHolder.getChildren().add(label);
    }

    /**
     * Sets arrow key for scrolling through command history
     */
    private void registerArrowKeyEventFilter() {
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode.equals(KeyCode.UP)) {
                String previousCommand = commandBoxHistory.getPreviousCommand();
                commandTextField.setText(previousCommand);
            } else if (keyCode.equals(KeyCode.DOWN)) {
                String nextCommand = commandBoxHistory.getNextCommand();
                commandTextField.setText(nextCommand);
            } else {
                return;
            }
            commandTextField.end();
            event.consume();
        });
    }
    
    /**
     * Sets tab key for autocomplete
     */
    private void registerTabKeyEventFilter() {
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode.equals(KeyCode.TAB)) {
                Optional<String> parsedString = EditDistanceCalculator.findCommandCompletion(commandTextField.getText());
                if(parsedString.isPresent()) {
                    commandTextField.setText(parsedString.get());
                }
            } else {
                return;
            }
            commandTextField.end();
            event.consume();
        });
    }

    /**
     * Sets the command box style to indicate a correct command.
     */
    private void setStyleToIndicateCorrectCommand() {
        commandTextField.getStyleClass().remove("error");
        commandTextField.setText("");
    }

    @Subscribe
    private void handleIncorrectCommandAttempted(IncorrectCommandAttemptedEvent event){
        logger.info(LogsCenter.getEventHandlingLogMessage(
                event, "Invalid command: " + commandBoxHistory.getLastCommand()));
        setStyleToIndicateIncorrectCommand();
        restoreCommandText();
    }

    /**
     * Restores the command box text to the previously entered command
     */
    private void restoreCommandText() {
        commandTextField.setText(commandBoxHistory.getLastCommand());
        commandTextField.selectEnd();
    }

    /**
     * Sets the command box style to indicate an error
     */
    private void setStyleToIndicateIncorrectCommand() {
        commandTextField.getStyleClass().add(ERROR);
    }

}
