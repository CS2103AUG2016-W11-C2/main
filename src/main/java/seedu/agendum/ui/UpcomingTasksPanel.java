package seedu.agendum.ui;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.Task;

//@@author A0148031R
/**
 * Panel contains the list of all uncompleted tasks with time
 */
public class UpcomingTasksPanel extends TasksPanel {
    private static final String FXML = "UpcomingTasksPanel.fxml";
    private static ObservableList<ReadOnlyTask> mainTaskList;

    @FXML
    private ListView<ReadOnlyTask> upcomingTasksListView;

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    protected void setConnections(ObservableList<ReadOnlyTask> taskList) {
        mainTaskList = taskList;
        upcomingTasksListView.setItems(taskList.filtered(task -> task.hasTime() && !task.isCompleted()));
        upcomingTasksListView.setCellFactory(listView -> new upcomingTasksListViewCell());
    }

    public void scrollTo(Task task, boolean isMultipleTasks) {
        Platform.runLater(() -> {
            upcomingTasksListView.scrollTo(mainTaskList.indexOf(task));
            if(isMultipleTasks) {
                upcomingTasksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                upcomingTasksListView.getSelectionModel().select(mainTaskList.indexOf(task));
            } else {
                upcomingTasksListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                upcomingTasksListView.getSelectionModel().clearAndSelect(mainTaskList.indexOf(task));
            }
        });
    }

    class upcomingTasksListViewCell extends ListCell<ReadOnlyTask> {

        public upcomingTasksListViewCell() {
            prefWidthProperty().bind(upcomingTasksListView.widthProperty());
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
