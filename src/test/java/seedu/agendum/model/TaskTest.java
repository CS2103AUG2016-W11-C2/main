package seedu.agendum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.Name;
import seedu.agendum.model.task.Task;

public class TaskTest {

    private Task uncompletedTask;
    private Task eventTask;
    private Optional<LocalDateTime> yesterday = 
            Optional.ofNullable(LocalDateTime.now().minusDays(1));
    private Optional<LocalDateTime> tomorrow = 
            Optional.ofNullable(LocalDateTime.now().plusDays(1));
    
    @Before
    public void setup() {
        try {
            uncompletedTask = new Task(new Name("task"));
            uncompletedTask.setLastUpdatedTime(yesterday.get());
            eventTask = new Task(new Name("task"), yesterday, tomorrow);
            eventTask.setLastUpdatedTime(yesterday.get());
        } catch (IllegalValueException e) {
            assert false : "valid name is given";
        }
    }
    
    @Test
    public void isOverdue_floatingTask_returnsFalse() {
        assertFalse(uncompletedTask.isOverdue());
    }

    @Test
    public void isOverdue_completedTask_returnsFalse() {
        // testing for completion status, give valid end date time
        uncompletedTask.setEndDateTime(yesterday);
        uncompletedTask.markAsCompleted();
        assertFalse(uncompletedTask.isOverdue());
    }

    @Test
    public void isOverdue_uncompletedTaskFromYesterday_returnsTrue() {
        uncompletedTask.setEndDateTime(yesterday);
        assertTrue(uncompletedTask.isOverdue());
    }

    @Test
    public void isUpcoming_floatingTask_returnsFalse() {
        assertFalse(uncompletedTask.isUpcoming());
    }

    @Test
    public void isUpcoming_completedTask_returnsFalse() {
        // testing for completion status, give valid end date time
        uncompletedTask.setEndDateTime(tomorrow);
        uncompletedTask.markAsCompleted();
        assertFalse(uncompletedTask.isUpcoming());
    }

    @Test
    public void isUpcoming_overdueTask_returnsFalse() {
        uncompletedTask.setEndDateTime(yesterday);
        assertFalse(uncompletedTask.isUpcoming());
    }
    
    @Test
    public void isUpcoming_uncompletedTaskFromTomorrow_returnsTrue() {
        uncompletedTask.setEndDateTime(tomorrow);
        assertTrue(uncompletedTask.isUpcoming());
    }

    @Test
    public void isEvent_floatingTask_returnsFalse() {
        assertFalse(uncompletedTask.isEvent());
    }

    @Test
    public void isEvent_noStartTime_returnsFalse() {
        uncompletedTask.setEndDateTime(tomorrow);
        assertFalse(uncompletedTask.isEvent());
    }

    @Test
    public void isEvent_hasStartAndEndTime_returnsFalse() {
        assertTrue(eventTask.isEvent());
    }

    @Test
    public void hasDeadline_floatingTask_returnsFalse() {
        assertFalse(uncompletedTask.hasDeadline());
    }

    @Test
    public void hasDeadline_noStartTime_returnsTrue() {
        uncompletedTask.setEndDateTime(tomorrow);
        assertTrue(uncompletedTask.hasDeadline());
    }

    @Test
    public void hasDeadline_hasStartAndEndTime_returnsFalse() {
        assertFalse(eventTask.hasDeadline());
    }

    @Test
    public void setName_updateNameAndPreserveProperties() {
        try {
            Task copiedTask = new Task(eventTask);
            eventTask.setName(new Name("updated task"));
            assertEquals(eventTask.getName().toString(), "updated task");
            assertEquals(eventTask.getStartDateTime(), copiedTask.getStartDateTime());
            assertEquals(eventTask.getEndDateTime(), copiedTask.getEndDateTime());
            assertEquals(eventTask.isCompleted(), copiedTask.isCompleted());
            assertTrue(eventTask.getLastUpdatedTime()
                    .isAfter(copiedTask.getLastUpdatedTime()));
        } catch (IllegalValueException e) {
            assert false: "valid name is given";
        }
    }

    @Test
    public void setStartTime_updateStartTimeAndPreserveProperties() {
        Task copiedTask = new Task(eventTask);
        eventTask.setStartDateTime(Optional.empty());
        assertEquals(eventTask.getName(), copiedTask.getName());
        assertEquals(eventTask.getStartDateTime(), Optional.empty());
        assertEquals(eventTask.getEndDateTime(), copiedTask.getEndDateTime());
        assertEquals(eventTask.isCompleted(), copiedTask.isCompleted());
        assertTrue(eventTask.getLastUpdatedTime().isAfter(copiedTask.getLastUpdatedTime()));
    }

    @Test
    public void setEndTime_updateEndTimeAndPreserveProperties() {
        Task copiedTask = new Task(eventTask);
        eventTask.setEndDateTime(yesterday);
        assertEquals(eventTask.getName(), copiedTask.getName());
        assertEquals(eventTask.getStartDateTime(), copiedTask.getStartDateTime());
        assertEquals(eventTask.getEndDateTime(), yesterday);
        assertEquals(eventTask.isCompleted(), copiedTask.isCompleted());
        assertTrue(eventTask.getLastUpdatedTime().isAfter(copiedTask.getLastUpdatedTime()));
    }

    @Test
    public void markAsCompleted_markAsCompletedAndPreserveProperties() {
        Task copiedTask = new Task(eventTask);
        eventTask.markAsCompleted();
        assertEquals(eventTask.getName(), copiedTask.getName());
        assertEquals(eventTask.getStartDateTime(), copiedTask.getStartDateTime());
        assertEquals(eventTask.getEndDateTime(), copiedTask.getEndDateTime());
        assertTrue(eventTask.isCompleted());
        assertTrue(eventTask.getLastUpdatedTime().isAfter(copiedTask.getLastUpdatedTime()));
    }

    @Test
    public void sort_uncompletedAndCompletedTasks_uncompletedFirst() {
        Task completedTask = new Task(uncompletedTask);
        completedTask.markAsCompleted();
        // make last updated time constant
        completedTask.setLastUpdatedTime(yesterday.get());

        List<Task> expectedList = Arrays.asList(uncompletedTask, completedTask);
        
        List<Task> tasks = Arrays.asList(completedTask, uncompletedTask);
        Collections.sort(tasks);
        
        assertEquals(expectedList, tasks);        
    }

    @Test
    // Sort uncompleted tasks based on their task time (start time if present, else end time)
    // Regardless of their last updated time
    // Floating tasks come last
    public void sort_uncompletedTasks_earlierTaskTimeFirst() {
        Task earlierTask = new Task(uncompletedTask);
        earlierTask.setEndDateTime(yesterday);

        Task laterTask = new Task(uncompletedTask);
        laterTask.setEndDateTime(tomorrow);

        List<Task> expectedList = Arrays.asList(earlierTask, laterTask, uncompletedTask);
        
        List<Task> tasks = Arrays.asList(laterTask, uncompletedTask, earlierTask);
        Collections.sort(tasks);
        
        assertEquals(expectedList, tasks);        
    }

    @Test
    // Sort completed tasks based on their last updated time
    // Tasks with later updated time comes first
    // Regardless of their task time (start/end time)
    public void sort_completedTasks_earlierUpdatedTimeFirst() {
        Task earlierUpdatedTask = new Task(uncompletedTask);
        earlierUpdatedTask.markAsCompleted();
        earlierUpdatedTask.setEndDateTime(tomorrow);
        earlierUpdatedTask.setLastUpdatedTime(yesterday.get());

        Task laterUpdatedTask = new Task(uncompletedTask);
        laterUpdatedTask.markAsCompleted();
        laterUpdatedTask.setEndDateTime(yesterday);
        laterUpdatedTask.setLastUpdatedTime(tomorrow.get());

        List<Task> expectedList = Arrays.asList(laterUpdatedTask, earlierUpdatedTask);
        
        List<Task> tasks = Arrays.asList(earlierUpdatedTask, laterUpdatedTask);
        Collections.sort(tasks);
        
        assertEquals(expectedList, tasks);        
    }
}
