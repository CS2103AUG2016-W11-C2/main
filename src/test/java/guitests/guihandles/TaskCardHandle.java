package guitests.guihandles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import guitests.GuiRobot;
import javafx.scene.Node;
import javafx.stage.Stage;
import seedu.agendum.model.task.ReadOnlyTask;

//@@author A0148031R
/**
 * Provides a handle to a task card in the task list panel.
 */
public class TaskCardHandle extends GuiHandle {
    private static final String NAME_FIELD_ID = "#name";
    private static final String INDEX_FIELD_ID = "#id";
    private static final String TIME_FIELD_ID = "#time";
    private static final String NON_COMPLETED_TIME_PATTERN = "HH:mm EEE, dd MMM";
    private static final String COMPLETED_TIME_PATTERN = "EEE, dd MMM";
    private static final String START_TIME_PREFIX = " from ";
    private static final String END_TIME_PREFIX = " to ";
    private static final String DEADLINE_PREFIX = "by ";
    private static final String EMPTY_PREFIX = "";
    private static final String OVERDUE_PREFIX = "Overdue\n";
    private static final String COMPLETED_PREFIX = "Completed ";

    private Node node;

    public TaskCardHandle(GuiRobot guiRobot, Stage primaryStage, Node node){
        super(guiRobot, primaryStage, null);
        this.node = node;
    }

    protected String getTextFromLabel(String fieldId) {
        return getTextFromLabel(fieldId, node);
    }

    public String getName() {
        return getTextFromLabel(NAME_FIELD_ID);
    }

    public String getTaskIndex() {
        return getTextFromLabel(INDEX_FIELD_ID);
    }

    public String getTime() {
        return getTextFromLabel(TIME_FIELD_ID);
    }

    public boolean isSameTask(ReadOnlyTask task){
        // the completion status will be checked by which panel it belongs in
        if (task.isOverdue()) {
            return getName().equals(task.getName().fullName) && getTime().equals(OVERDUE_PREFIX
                    + formatTime(task, NON_COMPLETED_TIME_PATTERN, START_TIME_PREFIX, task.getStartDateTime())
                    + formatTime(task, NON_COMPLETED_TIME_PATTERN, END_TIME_PREFIX, task.getEndDateTime()));
        } else if (task.hasTime()) {
            return getName().equals(task.getName().fullName) && getTime()
                    .equals(formatTime(task, NON_COMPLETED_TIME_PATTERN, START_TIME_PREFIX, task.getStartDateTime())
                            + formatTime(task, NON_COMPLETED_TIME_PATTERN, END_TIME_PREFIX, task.getEndDateTime()));
        } else if (task.isCompleted()) {
            return getName().equals(task.getName().fullName) && getTime().equals(COMPLETED_PREFIX + formatTime(task,
                    COMPLETED_TIME_PATTERN, EMPTY_PREFIX, Optional.ofNullable(task.getLastUpdatedTime())));
        } else {
            return getName().equals(task.getName().fullName);
        }
    }
    

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TaskCardHandle) {
            TaskCardHandle handle = (TaskCardHandle) obj;
            return getName().equals(handle.getName())
                && getTaskIndex().equals(handle.getTaskIndex())
                && getTime().equals(handle.getTime());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return getTaskIndex() + " " + getName() + "Time: " + getTime();
    }

    public String formatTime(ReadOnlyTask task, String dateTimePattern, String prefix, Optional<LocalDateTime> dateTime) {
        
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
}
