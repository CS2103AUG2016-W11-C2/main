package seedu.agendum.model;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.util.StringUtil;
import seedu.agendum.model.task.Name;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.Task;
import seedu.agendum.model.task.UniqueTaskList;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;
import seedu.agendum.commons.events.model.ToDoListChangedEvent;
import seedu.agendum.commons.core.ComponentManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the to do list data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ToDoList toDoList;
    private final FilteredList<Task> filteredTasks;
    private final FilteredList<Task> completedTasks;
    private final SortedList<Task> sortedTasks;
    private final FilteredList<Task> uncompletedUpcomingTasks;
    private final FilteredList<Task> uncompletedOverdueTasks;
    private final SortedList<Task> upcomingTasks;
    private final SortedList<Task> overdueTasks;

    /**
     * Initializes a ModelManager with the given ToDoList
     * ToDoList and its variables should not be null
     */
    public ModelManager(ToDoList src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

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
    }

    public ModelManager() {
        this(new ToDoList(), new UserPrefs());
    }

    public ModelManager(ReadOnlyToDoList initialData, UserPrefs userPrefs) {
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
    }

    @Override
    public void resetData(ReadOnlyToDoList newData) {
        toDoList.resetData(newData);
        indicateToDoListChanged();
    }

    @Override
    public ReadOnlyToDoList getToDoList() {
        return toDoList;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateToDoListChanged() {
        raise(new ToDoListChangedEvent(toDoList));
    }

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        toDoList.removeTask(target);
        indicateToDoListChanged();
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        toDoList.addTask(task);
        updateFilteredListToShowAll();
        indicateToDoListChanged();
    }

    @Override
    public synchronized void updateTask(ReadOnlyTask target, Task updatedTask)
            throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException {
        toDoList.updateTask(target, updatedTask);
        updateFilteredListToShowAll();
        indicateToDoListChanged();
    }

    @Override
    public synchronized void markTask(ReadOnlyTask target) throws TaskNotFoundException {
        toDoList.markTask(target);
        indicateToDoListChanged();
    }
    
    @Override
    public synchronized void unmarkTask(ReadOnlyTask target) throws TaskNotFoundException {
        toDoList.unmarkTask(target);
        indicateToDoListChanged();
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
