package seedu.agendum.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import seedu.agendum.model.task.ReadOnlyTask;

//@@author A0148031R
public class TaskCard extends UiPart {
    
    private static final String FXML = "TaskCard.fxml";
    private static final String OVERDUE_PREFIX = "Overdue\n";
    private static final String COMPLETED_PREFIX = "Completed ";
    private static final String NAME_COLOR = "#3a3d42";
    private static final String TIME_COLOR = "#4172c1";
    private static final String NON_COMPLETED_TIME_PATTERN = "HH:mm EEE, dd MMM";
    private static final String COMPLETED_TIME_PATTERN = "EEE, dd MMM";
    private static final String START_TIME_PREFIX = "from ";
    private static final String END_TIME_PREFIX = " to ";
    private static final String DEADLINE_PREFIX = "by ";
    private static final String EMPTY_PREFIX = "";
    
    @FXML
    private HBox cardPane;
    @FXML
    private VBox taskVbox;
    @FXML
    private Label name;
    @FXML
    private Label id;

    private ReadOnlyTask task;
    private String displayedIndex;

    public TaskCard() {}

    public static TaskCard load(ReadOnlyTask task, int Index){
        TaskCard card = new TaskCard();
        card.task = task;
        card.displayedIndex = String.valueOf(Index) + ".";
        return UiPartLoader.loadUiPart(card);
    }

    @FXML
    public void initialize() {
        
        if(task.isOverdue()) {
            cardPane.setStyle("-fx-background-color: rgb(255, 169, 147)");
        } else if(task.isUpcoming()) {
            cardPane.setStyle("-fx-background-color: rgb(255, 229, 86)");
        } else {
            cardPane.setStyle("-fx-background-color: rgba(255,255,255,0.6)");
        }
        
        name.setTextFill(Color.web(NAME_COLOR));
        name.setText(task.getName().fullName);
        id.setText(displayedIndex);
        
        Label time = new Label();
        time.setMaxHeight(Control.USE_COMPUTED_SIZE);
        time.setTextFill(Color.web(TIME_COLOR));
        time.setWrapText(true);
        
        if(task.isOverdue()) {
            time.setText(OVERDUE_PREFIX 
                    + formatTime(NON_COMPLETED_TIME_PATTERN, START_TIME_PREFIX, task.getStartDateTime()) 
                    + formatTime(NON_COMPLETED_TIME_PATTERN, END_TIME_PREFIX, task.getEndDateTime()));
        } else if(task.hasTime()){
            time.setText(formatTime(NON_COMPLETED_TIME_PATTERN, START_TIME_PREFIX, task.getStartDateTime()) 
                    + formatTime(NON_COMPLETED_TIME_PATTERN, END_TIME_PREFIX, task.getEndDateTime()));
        } else if(task.isCompleted()) {
            time.setText(COMPLETED_PREFIX 
                    + formatTime(COMPLETED_TIME_PATTERN, EMPTY_PREFIX, 
                            Optional.ofNullable(task.getLastUpdatedTime())));
        }
        
        if(task.hasTime() || task.isCompleted()) {
            taskVbox.getChildren().add(time);
            taskVbox.setAlignment(Pos.CENTER_LEFT);
            time.setAlignment(Pos.CENTER_LEFT);
            time.setFont(Font.font("Verdana", FontPosture.ITALIC, 11));
        }
    }
    
    public String formatTime(String dateTimePattern, String prefix, Optional<LocalDateTime> dateTime) {
        
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter format = DateTimeFormatter.ofPattern(dateTimePattern);
        
        if(task.isCompleted()) {
            sb.append(dateTime.get().format(format));
        } else if (dateTime.isPresent() && task.getStartDateTime().isPresent()) {
            sb.append(prefix).append(dateTime.get().format(format));
        } else if(dateTime.isPresent()) {
            sb.append(DEADLINE_PREFIX).append(dateTime.get().format(format));
        } else {
            sb.append(EMPTY_PREFIX);
        }
        
        return sb.toString().toLowerCase();
    }

    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
