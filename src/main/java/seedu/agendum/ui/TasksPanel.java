package seedu.agendum.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.Task;

//@@author A0148031R
/**
 * Panel that contains the list of tasks
 */
public abstract class TasksPanel extends UiPart{
    private AnchorPane panel;
    private AnchorPane placeHolderPane;
    
    public TasksPanel() {
        super();
    }
    
    @Override
    public void setNode(Node node) {
        panel = (AnchorPane) node;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }
    
    public static TasksPanel load(Stage primaryStage, AnchorPane tasksPlaceholder,
            ObservableList<ReadOnlyTask> taskList, TasksPanel tasksPanelType) {
        TasksPanel tasksPanel = UiPartLoader.loadUiPart(primaryStage, tasksPlaceholder, tasksPanelType);
        tasksPanel.configure(taskList);
        return tasksPanel;
    }
    
    private void configure(ObservableList<ReadOnlyTask> taskList) {
        setConnections(taskList);
        addToPlaceholder();
    }
    
    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(panel);
    }
    
    protected abstract void setConnections(ObservableList<ReadOnlyTask> allTasks);
    
    public abstract void scrollTo(Task task, boolean hasMultipleTasks);
    
}
