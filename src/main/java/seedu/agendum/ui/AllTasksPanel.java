package seedu.agendum.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.commons.core.LogsCenter;

//@@author A0148031R
/**
 * Panel contains the list of all tasks
 */
public class AllTasksPanel extends TasksPanel {
    private final Logger logger = LogsCenter.getLogger(AllTasksPanel.class);
    private static final String FXML = "AllTasksPanel.fxml";

    @FXML
    private ListView<ReadOnlyTask> allTasksListView;

    public AllTasksPanel() {
        super();
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    protected void setConnections(ObservableList<ReadOnlyTask> allTasks) {
        allTasksListView.setItems(allTasks);
        allTasksListView.setCellFactory(listView -> new allTasksListViewCell());
    }

    public void scrollTo(int index) {
        Platform.runLater(() -> {
            allTasksListView.scrollTo(index);
            allTasksListView.getSelectionModel().clearAndSelect(index);
        });
    }

    class allTasksListViewCell extends ListCell<ReadOnlyTask> {

        public allTasksListViewCell() {
        }

        @Override
        protected void updateItem(ReadOnlyTask task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(TaskCard.load(task, getIndex() + 1).getLayout());
            }
        }
    }

}
