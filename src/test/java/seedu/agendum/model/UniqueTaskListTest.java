package seedu.agendum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.Name;
import seedu.agendum.model.task.Task;
import seedu.agendum.model.task.UniqueTaskList;
import seedu.agendum.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.agendum.model.task.UniqueTaskList.TaskNotFoundException;

public class UniqueTaskListTest {

    private UniqueTaskList uniqueTaskList;
    private Task originalTask;
    private Task duplicateOfOriginalTask;
    private Task newTask;

    @Before
    public void setup() {
        try {
            uniqueTaskList = new UniqueTaskList();
            originalTask = new Task(new Name("task"));
            duplicateOfOriginalTask = new Task(originalTask);
            newTask = new Task(new Name("new task"));
            uniqueTaskList.add(originalTask);
        } catch (IllegalValueException e) {
            assert false : "valid name is given";
        }
    }

    @Test
    public void contains_taskInEmptyList_returnsFalse() {
        uniqueTaskList = new UniqueTaskList();
        assertFalse(uniqueTaskList.contains(originalTask));
    }

    @Test
    public void contains_taskWithDifferentState_returnsFalse() throws Exception {
        assertFalse(uniqueTaskList.contains(newTask));
    }

    @Test
    public void contains_taskWithSameState_returnsTrue() throws Exception {
        assertTrue(uniqueTaskList.contains(duplicateOfOriginalTask));
    }

    @Test(expected = DuplicateTaskException.class)
    public void add_duplicateTask_throwsException() throws Exception {
        uniqueTaskList.add(duplicateOfOriginalTask);
    }

    @Test
    public void add_newTask_successful() throws Exception {
        uniqueTaskList.add(newTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void remove_absentTask_throwsException() throws Exception {
        uniqueTaskList.remove(newTask);
    }

    @Test
    public void remove_existingTask_successful() throws Exception {
        uniqueTaskList.remove(duplicateOfOriginalTask);
    }

    @Test(expected = DuplicateTaskException.class)
    public void update_duplicateTask_throwsException() throws Exception {
        uniqueTaskList.add(newTask);
        uniqueTaskList.update(newTask, duplicateOfOriginalTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void update_absentTask_throwsException() throws Exception {
        uniqueTaskList.update(newTask, newTask);
    }

    @Test
    public void update_existingTask_successful() throws Exception {
        uniqueTaskList.update(originalTask, newTask);
    }

    @Test(expected = DuplicateTaskException.class)
    public void mark_resultInDuplicateTask_throwsException() throws Exception {
        duplicateOfOriginalTask.markAsCompleted();
        uniqueTaskList.add(duplicateOfOriginalTask);
        uniqueTaskList.mark(originalTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void mark_absentTask_throwsException() throws Exception {
        uniqueTaskList.mark(newTask);
    }

    @Test
    public void mark_existingTask_successful() throws Exception {
        uniqueTaskList.mark(originalTask);
    }

    @Test(expected = DuplicateTaskException.class)
    public void unmark_resultInDuplicateTask_throwsException() throws Exception {
        // cannot unmark a task that have not been mark as completed
        uniqueTaskList.unmark(originalTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void unmark_absentTask_throwsException() throws Exception {
        uniqueTaskList.unmark(newTask);
    }

    @Test
    public void unmark_existingTask_successful() throws Exception {
        uniqueTaskList = new UniqueTaskList();
        duplicateOfOriginalTask.markAsCompleted();
        uniqueTaskList.add(duplicateOfOriginalTask);
        uniqueTaskList.unmark(duplicateOfOriginalTask);
    }
}