package seedu.agendum.model;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.util.ConfigUtil;
import seedu.agendum.commons.util.StringUtil;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.RecurringTask;
import seedu.agendum.model.task.Task;
import seedu.agendum.model.task.UniqueTaskList;
import seedu.agendum.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;
import seedu.agendum.commons.events.model.SaveLocationChangedEvent;
import seedu.agendum.commons.events.model.ToDoListChangedEvent;
import seedu.agendum.commons.core.ComponentManager;
import seedu.agendum.commons.core.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the to do list data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ToDoList toDoList;
    private final Stack<ToDoList> previousLists;
    private final FilteredList<Task> filteredTasks;
    private final FilteredList<Task> completedTasks;
    private final SortedList<Task> sortedTasks;
    private final FilteredList<Task> uncompletedUpcomingTasks;
    private final FilteredList<Task> uncompletedOverdueTasks;
    private final SortedList<Task> upcomingTasks;
    private final SortedList<Task> overdueTasks;
    private final Config config;

    /**
     * Initializes a ModelManager with the given ToDoList
     * ToDoList and its variables should not be null
     */
    public ModelManager(ToDoList src, UserPrefs userPrefs, Config config) {
        super();
        assert src != null;
        assert userPrefs != null;
        assert config != null;

        logger.fine("Initializing with to do list: " + src + " and user prefs " + userPrefs);

        toDoList = new ToDoList(src);
        filteredTasks = new FilteredList<>(toDoList.getTasks());
        sortedTasks = filteredTasks.sorted();
        completedTasks = new FilteredList<>(toDoList.getTasks());
        completedTasks.setPredicate(task -> task.isCompleted());
        uncompletedUpcomingTasks = new FilteredList<>(toDoList.getTasks());
        uncompletedOverdueTasks = new FilteredList<>(toDoList.getTasks());
        uncompletedUpcomingTasks.setPredicate(task -> task.isUpcoming());
        uncompletedOverdueTasks.setPredicate(task -> task.isOverdue());
        upcomingTasks = uncompletedUpcomingTasks.sorted();
        overdueTasks = uncompletedOverdueTasks.sorted();
        previousLists = new Stack<ToDoList>();
        backupNewToDoList();
        this.config = config;
    }

    public ModelManager() {
        this(new ToDoList(), new UserPrefs(), new Config());
    }

    public ModelManager(ReadOnlyToDoList initialData, UserPrefs userPrefs, Config config) {
        toDoList = new ToDoList(initialData);
        filteredTasks = new FilteredList<>(toDoList.getTasks());
        sortedTasks = filteredTasks.sorted();
        completedTasks = new FilteredList<>(toDoList.getTasks());
        completedTasks.setPredicate(task -> task.isCompleted());
        uncompletedUpcomingTasks = new FilteredList<>(toDoList.getTasks());
        uncompletedOverdueTasks = new FilteredList<>(toDoList.getTasks());
        uncompletedUpcomingTasks.setPredicate(task -> task.isUpcoming());
        uncompletedOverdueTasks.setPredicate(task -> task.isOverdue());
        upcomingTasks = uncompletedUpcomingTasks.sorted();
        overdueTasks = uncompletedOverdueTasks.sorted();
        previousLists = new Stack<ToDoList>();
        backupNewToDoList();
        this.config = config;
    }

    @Override
    public void resetData(ReadOnlyToDoList newData) {
        toDoList.resetData(newData);
        indicateToDoListChanged();
        backupNewToDoList();
    }

    @Override
    public ReadOnlyToDoList getToDoList() {
        return toDoList;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateToDoListChanged() {
        raise(new ToDoListChangedEvent(toDoList));
    }
    
    /** Raises an event to indicate that save location has changed */
    private void indicateSaveLocationChanged(String location) {
        raise(new SaveLocationChangedEvent(location));
    }

    @Override
    public synchronized void deleteTasks(ArrayList<ReadOnlyTask> targets) throws TaskNotFoundException {
        for (ReadOnlyTask target: targets) {
            toDoList.removeTask(target);
        }
        indicateToDoListChanged();
        backupNewToDoList();
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        toDoList.addTask(task);
        updateFilteredListToShowAll();
        indicateToDoListChanged();
        backupNewToDoList();
    }

    @Override
    public synchronized void changeSaveLocation(String location){
        assert StringUtil.isValidPathToFile(location);

        config.setToDoListFilePath(location);
        indicateSaveLocationChanged(location);
        saveConfigFile();
    }

    private void saveConfigFile() {
        try {
            ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }        
    }

    @Override
    public synchronized void updateTask(ReadOnlyTask target, Task updatedTask)
            throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException {
        toDoList.updateTask(target, updatedTask);
        updateFilteredListToShowAll();
        indicateToDoListChanged();
        backupNewToDoList();
    }

    @Override
    public synchronized void markTasks(ArrayList<ReadOnlyTask> targets) throws TaskNotFoundException {
        for (ReadOnlyTask target: targets) {
            if(target.isRecurring()) {
                try {
                    // Add a child recurring task that is already marked as completed, and update the time of parent
                    addTask(target.getChild());
                } catch (DuplicateTaskException e) {
                    e.printStackTrace();
                }
            } else {
                toDoList.markTask(target);
            }
        }
        indicateToDoListChanged();
        backupNewToDoList();
    }
    
    @Override
    public synchronized void unmarkTasks(ArrayList<ReadOnlyTask> targets) throws TaskNotFoundException {
        for (ReadOnlyTask target: targets) {
            if(target.isRecurring()) {
                // Delete the child recurring task, and update time of parent to previous
                target.getParent().setPreviousDateTime();
                ArrayList<ReadOnlyTask> taskToDelete = new ArrayList<ReadOnlyTask>();
                taskToDelete.add(target);
                deleteTasks(taskToDelete);
            } else {
                toDoList.unmarkTask(target);
            }
        }
        indicateToDoListChanged();
        backupNewToDoList();
    }

    @Override
    public synchronized boolean restorePreviousToDoList() {
        assert !previousLists.empty();
        if (previousLists.size() == 1) {
            return false;
        } else {
            previousLists.pop();
            toDoList.resetData(previousLists.peek());
            indicateToDoListChanged();
            return true;
        }
    }
 
    private void backupNewToDoList() {
        previousLists.push(new ToDoList(this.getToDoList()));
    }

    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
        return new UnmodifiableObservableList<>(sortedTasks);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredTasks.setPredicate(null);
    }
    
    @Override
    public void updateFilteredListToShowUncompleted() {
        filteredTasks.setPredicate(task -> !task.isCompleted());
    }

    @Override
    public void updateFilteredListToShowCompleted() {
        filteredTasks.setPredicate(task -> task.isCompleted());
    }

    @Override
    public void updateFilteredListToShowOverdue() {
        filteredTasks.setPredicate(task -> task.isOverdue());
    }

    @Override
    public void updateFilteredListToShowUpcoming() {
        filteredTasks.setPredicate(task -> task.isUpcoming());
    }

    @Override
    public void updateFilteredTaskList(Set<String> keywords){
        updateFilteredTaskList(new PredicateExpression(new NameQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        filteredTasks.setPredicate(expression::satisfies);
    }
    
    //=========== Other Task List Accessors ===================================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getCompletedTaskList() {
        return new UnmodifiableObservableList<>(completedTasks);
    }

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getUpcomingTaskList() {
        return new UnmodifiableObservableList<>(upcomingTasks);
    }

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getOverdueTaskList() {
        return new UnmodifiableObservableList<>(overdueTasks);
    }

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(ReadOnlyTask task);
        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(ReadOnlyTask task);
        String toString();
    }

    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;

        NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }

        @Override
        public boolean run(ReadOnlyTask task) {
            return nameKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(task.getName().fullName, keyword))
                    .findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }

}
