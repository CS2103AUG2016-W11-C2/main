package seedu.agendum.model;

import seedu.agendum.commons.core.UnmodifiableObservableList;
import seedu.agendum.commons.events.storage.LoadDataCompleteEvent;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.Task;
import seedu.agendum.model.task.UniqueTaskList;

import java.util.ArrayList;
import java.util.Set;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyToDoList newData);

    /** Returns the ToDoList */
    ReadOnlyToDoList getToDoList();

    /** Deletes the given task(s) */
    void deleteTasks(ArrayList<ReadOnlyTask> targets) throws UniqueTaskList.TaskNotFoundException;

    /** Adds the given task */
    void addTask(Task task) throws UniqueTaskList.DuplicateTaskException;
    
    /** Updates the given task */
    void updateTask(ReadOnlyTask target, Task updatedTask)
            throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException;
       
    /** Marks the given task(s) as completed */
    void markTasks(ArrayList<ReadOnlyTask> targets) throws UniqueTaskList.TaskNotFoundException;
    
    /** Unmarks the given task(s) */
    void unmarkTasks(ArrayList<ReadOnlyTask> targets) throws UniqueTaskList.TaskNotFoundException;

    /** Restores the previous to do list saved. Returns true if successful; false if no previous saved list*/
    boolean restorePreviousToDoList();
    
    /** Returns the filtered task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();

    /** Updates the filter of the filtered task list to show all tasks */
    void updateFilteredListToShowAll();

    /** Updates the filter of the filtered task list to show all uncompleted tasks */
    void updateFilteredListToShowUncompleted();

    /** Updates the filter of the filtered task list to show all completed tasks */
    void updateFilteredListToShowCompleted();

    /** Updates the filter of the filtered task list to show all overdue tasks */
    void updateFilteredListToShowOverdue();

    /** Updates the filter of the filtered task list to show all upcoming tasks */
    void updateFilteredListToShowUpcoming();

    /** Updates the filter of the filtered task list to filter by the given keywords*/
    void updateFilteredTaskList(Set<String> keywords);
    
    /** Change the data storage location */
    void changeSaveLocation(String location);

    /** load the data from a file **/
    void loadFromLocation(String location);

    /** Returns the completed task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getCompletedTaskList();

    /** Returns the upcoming task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getUpcomingTaskList();

    /** Returns the overdue task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getOverdueTaskList();

    /** Updates the current todolist to the loaded data**/
    public void handleLoadDataCompleteEvent(LoadDataCompleteEvent event);

}
