package seedu.agendum.model;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.util.StringUtil;
import seedu.agendum.commons.util.XmlUtil;
import seedu.agendum.commons.util.XmlUtil;
import seedu.agendum.model.task.ChildRecurringTask;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.RecurringTask;
import seedu.agendum.model.task.Task;
import seedu.agendum.model.task.UniqueTaskList;
import seedu.agendum.model.task.UniqueTaskList.CannotMarkRecurringTaskException;
import seedu.agendum.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.agendum.model.task.UniqueTaskList.NotLatestRecurringTaskException;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;
import seedu.agendum.commons.events.model.LoadDataRequestEvent;
import seedu.agendum.commons.events.model.ChangeSaveLocationRequestEvent;
import seedu.agendum.commons.events.model.ToDoListChangedEvent;
import seedu.agendum.commons.events.storage.LoadDataCompleteEvent;
import seedu.agendum.commons.core.ComponentManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

/**
 * Represents the in-memory model of the to do list data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ToDoList toDoList;
    private final Stack<ToDoList> previousLists;
    private final FilteredList<Task> filteredTasks;
    private final SortedList<Task> sortedTasks;

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
        previousLists = new Stack<ToDoList>();
        backupNewToDoList();
    }

    public ModelManager() {
        this(new ToDoList(), new UserPrefs());
    }

    public ModelManager(ReadOnlyToDoList initialData, UserPrefs userPrefs) {
        toDoList = new ToDoList(initialData);
        filteredTasks = new FilteredList<>(toDoList.getTasks());
        sortedTasks = filteredTasks.sorted();
        previousLists = new Stack<ToDoList>();
        backupNewToDoList();
    }

    //@@author A0133367E
    @Override
    public void resetData(ReadOnlyToDoList newData) {
        toDoList.resetData(newData);
        logger.fine("[MODEL] --- succesfully reset data of the to-do list");
        backupNewToDoList();
        indicateToDoListChanged();
    }
  
    //@@author
    @Override
    public ReadOnlyToDoList getToDoList() {
        return toDoList;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateToDoListChanged() {
        toDoList.resetData(toDoList);
        raise(new ToDoListChangedEvent(toDoList));
    }
    
    //@@author A0148095X
    /** Raises an event to indicate that save location has changed */
    private void indicateChangeSaveLocationRequest(String location) {
        raise(new ChangeSaveLocationRequestEvent(location));
    }
    
    /** Raises an event to indicate that save location has changed */
    private void indicateLoadDataRequest(String location) {
        raise(new LoadDataRequestEvent(location));
    }

    //@@author A0133367E
    @Override
    public synchronized void deleteTasks(List<ReadOnlyTask> targets) throws TaskNotFoundException {
        for (ReadOnlyTask target: targets) {
            toDoList.removeTask(target);
        }
        backupNewToDoList();
        updateFilteredListToShowAll();
        indicateToDoListChanged();
        logger.fine("[MODEL] --- succesfully deleted all specified targets from the to-do list");
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        toDoList.addTask(task);      
        if(!task.isChild()) {
            backupNewToDoList();
        }
        updateFilteredListToShowAll();
        indicateToDoListChanged();
        logger.fine("MODEL --- succesfully added the new task to the to-do list");
    }
    
    @Override
    public synchronized void updateTask(ReadOnlyTask target, Task updatedTask)
            throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException {
        if(target.isRecurring() && !target.isChild()) {
            updateRecurringTask(target,updatedTask);
        } else {
            toDoList.updateTask(target, updatedTask);
        }
        logger.fine("[MODEL] --- succesfully updated the target task in the to-do list");
        backupNewToDoList();
        updateFilteredListToShowAll();
        indicateToDoListChanged();
    }
    
    private void updateRecurringTask(ReadOnlyTask target, Task updatedTask) 
            throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException {
        List<ReadOnlyTask> tasks = toDoList.getTaskList();
        ArrayList<ReadOnlyTask> children = new ArrayList<ReadOnlyTask>();
        String parentName = target.getName().fullName;
        
        for(ReadOnlyTask task : tasks) {
            if(task.isChild() && task.getName().fullName.equals(parentName)) {
                children.add(task);
            }
        }
        
        RecurringTask newParent = new RecurringTask(target);
        newParent.setName(updatedTask.getName());
        toDoList.updateTask(target, newParent);
        
        for(ReadOnlyTask task : children) {
            ChildRecurringTask newChild = new ChildRecurringTask(newParent);
            newChild.setStartDateTime(task.getStartDateTime());
            newChild.setEndDateTime(task.getEndDateTime());
            toDoList.updateTask(task, newChild);
        }
        
    }

    @Override
    public synchronized void markTasks(List<ReadOnlyTask> targets) throws TaskNotFoundException, DuplicateTaskException {
        for (ReadOnlyTask target : targets) {
            if (target.isRecurring() && !target.isChild()) {
                addTask(target.getChild());
            } else if (!target.isRecurring()) {
                toDoList.markTask(target);
            }
        }
        backupNewToDoList();
        indicateToDoListChanged();
        logger.fine("MODEL --- succesfully marked all specified targets from the to-do list");
    }
    
    @Override
    public synchronized void unmarkTasks(List<ReadOnlyTask> targets) throws TaskNotFoundException, 
    NotLatestRecurringTaskException, CannotMarkRecurringTaskException {
        for (ReadOnlyTask target: targets) {
            if (target.isChild() && !target.isLatestChild()) {
                throw new NotLatestRecurringTaskException();
            } else if(target.isLatestChild()) {
                // Delete the child recurring task, and update time of parent to previous
                target.getParent().setPreviousDateTime();
                ArrayList<ReadOnlyTask> taskToDelete = new ArrayList<ReadOnlyTask>();
                taskToDelete.add(target);
                deleteTasks(taskToDelete);
            } else if(target.isRecurring()) {
                throw new CannotMarkRecurringTaskException();
            } else {
                toDoList.unmarkTask(target);
            }
        }
        logger.fine("[MODEL] --- succesfully unmarked all specified targets from the to-do list");
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
            restorePreviousToDoListTaskType(previousLists.peek());
            logger.fine("[MODEL] --- succesfully restored the previous the to-do list from this session");
            indicateToDoListChanged();
            return true;
        }
    }

    @Override
    public void restorePreviousToDoListTaskType(ReadOnlyToDoList previousToDoList) {
        List<ReadOnlyTask> readOnlyTasks = previousToDoList.getTaskList();
        List<Task> tasks = new ArrayList<Task>();
        HashMap<String, RecurringTask> parents = new HashMap<String, RecurringTask>();
        List<ReadOnlyTask> childWaitingList = new ArrayList<ReadOnlyTask>();
        
        for (ReadOnlyTask task : readOnlyTasks) {
            if (task.isRecurring() && task.isChild()) {
                childWaitingList.add(task);
            } else if (task.isRecurring()) {
                RecurringTask parent = new RecurringTask(task);
                tasks.add(parent);
                parents.put(task.getName().fullName, parent);
            } else {
                tasks.add(new Task(task));
            }
        }

        for (ReadOnlyTask child : childWaitingList) {
            if (parents.containsKey(child.getName().fullName)) {
                RecurringTask parent = parents.get(child.getName().fullName);
                ChildRecurringTask newChild = new ChildRecurringTask(parent);
                newChild.setEndDateTime(child.getEndDateTime());
                newChild.setStartDateTime(child.getStartDateTime());
                tasks.add(newChild);
            } else {
                tasks.add(new Task(child));
            }
        }
        
        toDoList.setTasks(tasks);
    }
 
    private void backupNewToDoList() {
        ToDoList latestList = new ToDoList(this.getToDoList());
        previousLists.push(latestList);
    }


    //=========== Storage Methods ==========================================================================
    
    //@@author A0148095X
    @Override
    public synchronized void changeSaveLocation(String location){
        assert StringUtil.isValidPathToFile(location);
        indicateChangeSaveLocationRequest(location);
    }

    //@@author A0148095X
    @Override
    public synchronized void loadFromLocation(String location) {
        assert StringUtil.isValidPathToFile(location);
        assert XmlUtil.isFileCorrectFormat(location);
        
        changeSaveLocation(location);
        indicateLoadDataRequest(location);
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

    //========== event handling ==================================================
    //@@author A0148095X
    @Override
    @Subscribe
    public void handleLoadDataCompleteEvent(LoadDataCompleteEvent event) {
        this.toDoList.resetData(event.data);
        indicateToDoListChanged();
        logger.info("Loading completed - Todolist updated.");
    }
}
