package seedu.agendum.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Duration;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.Task;

//@@author A0148031R
/**
 * Panel contains the list of completed tasks
 */
public class CompletedTasksPanel extends TasksPanel {
    private static final String FXML = "CompletedTasksPanel.fxml";
    private static ObservableList<ReadOnlyTask> mainTaskList;

    @FXML
    private ListView<ReadOnlyTask> completedTasksListView;

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    protected void setConnections(ObservableList<ReadOnlyTask> taskList) {
        mainTaskList = taskList;
        completedTasksListView.setItems(taskList.filtered(task -> task.isCompleted()));
        completedTasksListView.setCellFactory(listView -> new CompletedTasksListViewCell());
    }

    public void scrollTo(Task task, boolean isMultipleTasks) {
        Platform.runLater(() -> {
            int index = mainTaskList.indexOf(task) - mainTaskList.filtered(t -> !t.isCompleted()).size();
            completedTasksListView.scrollTo(index);
            if(isMultipleTasks) {
                completedTasksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                completedTasksListView.getSelectionModel().select(index);
            } else {
                completedTasksListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                completedTasksListView.getSelectionModel().clearAndSelect(index);
            }
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> completedTasksListView.getSelectionModel().clearSelection(index));
            delay.play();
        });
    }
    
    class CompletedTasksListViewCell extends ListCell<ReadOnlyTask> {
        public CompletedTasksListViewCell() {
            prefWidthProperty().bind(completedTasksListView.widthProperty());
            setMaxWidth(Control.USE_PREF_SIZE);
        }

        @Override
        protected void updateItem(ReadOnlyTask task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(TaskCard.load(task, mainTaskList.indexOf(task) + 1).getLayout());
            }
        }
    }
}
