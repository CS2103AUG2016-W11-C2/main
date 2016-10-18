package seedu.agendum.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import seedu.agendum.logic.commands.AddCommand;
import seedu.agendum.logic.commands.ClearCommand;
import seedu.agendum.logic.commands.Command;
import seedu.agendum.logic.commands.DeleteCommand;
import seedu.agendum.logic.commands.ExitCommand;
import seedu.agendum.logic.commands.FindCommand;
import seedu.agendum.logic.commands.HelpCommand;
import seedu.agendum.logic.commands.ListCommand;
import seedu.agendum.logic.commands.MarkCommand;
import seedu.agendum.logic.commands.RenameCommand;
import seedu.agendum.logic.commands.UndoCommand;
import seedu.agendum.logic.commands.UnmarkCommand;
import seedu.agendum.commons.core.LogsCenter;

import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart {

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String ICON = "/images/help_icon.png";
    private static final String FXML = "HelpWindow.fxml";
    private static final String TITLE = "Help";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 650;
    private ObservableList<Command> commandList = FXCollections.observableArrayList();

    private AnchorPane mainPane;

    private Stage dialogStage;
    
    @FXML
    private TableView<Command> commandTable;
    
    @FXML
    private TableColumn<Command, String> commandColumn;
    
    @FXML
    private TableColumn<Command, String> descriptionColumn;
    
    @FXML
    private TableColumn<Command, String> formatColumn;
    
    @FXML
    private Button backButton;
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        
        backButton.setOnAction((event) -> {
            dialogStage.close();
        });
        
        commandColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getDescription()));
        formatColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getFormat()));
        
        commandTable.setItems(commandList);
    }
    
    public static HelpWindow load(Stage primaryStage) {
        logger.fine("Showing help page about the application.");
        HelpWindow helpWindow = UiPartLoader.loadUiPart(primaryStage, new HelpWindow());
        helpWindow.configure();
        return helpWindow;
    }
    

    @Override
    public void setNode(Node node) {
        mainPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    private void configure(){
        Scene scene = new Scene(mainPane);
        //Null passed as the parent stage to make it non-modal.
        dialogStage = createDialogStage(TITLE, null, scene);
        dialogStage.setWidth(WIDTH);
        dialogStage.setHeight(HEIGHT);
        dialogStage.setResizable(false);
        
        scene.setFill(Color.TRANSPARENT);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        
        setIcon(dialogStage, ICON);
        loadHelpList();
        
        handleEscape(scene);
    }

    private void handleEscape(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent evt) {
                if (evt.getCode().equals(KeyCode.ESCAPE)) {
                    dialogStage.close();
                }
            }
        });
    }

    private void loadHelpList() {
       commandList.add(new AddCommand());
       commandList.add(new RenameCommand());
       commandList.add(new MarkCommand(new HashSet<Integer>()));
       commandList.add(new UnmarkCommand(new HashSet<Integer>()));
       commandList.add(new DeleteCommand(new HashSet<Integer>()));
       commandList.add(new UndoCommand());
       commandList.add(new ListCommand());
       commandList.add(new FindCommand());
       commandList.add(new ClearCommand());
       commandList.add(new HelpCommand());
       commandList.add(new ExitCommand());
    }

    public void show() {
        dialogStage.showAndWait();
    }
}
