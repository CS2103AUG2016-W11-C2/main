package seedu.agendum.model;

import javafx.collections.transformation.FilteredList;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.util.ConfigUtil;
import seedu.agendum.commons.util.StringUtil;
import seedu.agendum.model.task.Name;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.Task;
import seedu.agendum.model.task.UniqueTaskList;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;
import seedu.agendum.commons.events.model.SaveLocationChangedEvent;
import seedu.agendum.commons.events.model.ToDoListChangedEvent;
import seedu.agendum.commons.core.ComponentManager;
import seedu.agendum.commons.core.Config;

import java.io.IOException;
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
        this.config = config;
    }

    public ModelManager() {
        this(new ToDoList(), new UserPrefs(), new Config());
    }

    public ModelManager(ReadOnlyToDoList initialData, UserPrefs userPrefs, Config config) {
        toDoList = new ToDoList(initialData);
        filteredTasks = new FilteredList<>(toDoList.getTasks());
        this.config = config;
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
    
    /** Raises an event to indicate that save location has changed */
    private void indicateSaveLocationChanged(String location) {
        raise(new SaveLocationChangedEvent(location));
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
    public synchronized void changeSaveLocation(String location) {
        assert !location.isEmpty();
        assert location != null;
        assert StringUtil.isValidFilePath(location);
        
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
    public synchronized void renameTask(ReadOnlyTask target, Name newTaskName)
            throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException {
        toDoList.renameTask(target, newTaskName);
        updateFilteredListToShowAll();
        indicateToDoListChanged();
    }

    @Override
    public synchronized void scheduleTask(ReadOnlyTask target, Optional<LocalDateTime> startDateTime,
            Optional<LocalDateTime> endDateTime) throws UniqueTaskList.TaskNotFoundException {
        toDoList.scheduleTask(target, startDateTime, endDateTime);
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
        return new UnmodifiableObservableList<>(filteredTasks);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredTasks.setPredicate(null);
    }

    @Override
    public void updateFilteredTaskList(Set<String> keywords){
        updateFilteredTaskList(new PredicateExpression(new NameQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        filteredTasks.setPredicate(expression::satisfies);
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
