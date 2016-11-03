package seedu.agendum.testutil;

import java.time.LocalDateTime;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.ToDoList;
import seedu.agendum.model.task.Task;

public class TypicalTestTasks {

    public static final String alice =  "meet Alice Pauline";
    public static final String benson = "meet Benson Meier";
    public static final String carl = "meet Carl Kurz";
    public static final String daniel = "meet Daniel Meier";
    public static final String elle = "meet Elle Meyer";
    public static final String fiona = "meet Fiona Kunz";
    public static final String george = "meet George Best";
    public static final String hoon = "meet Hoon Meier";
    public static final String ida = "meet Ida Mueller";;
    
    private static final LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    private static final LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);


    public static void loadToDoListWithSampleData(ToDoList tdl) {
        try {
            tdl.addTask(new Task(getTaskWithName(alice)));
            tdl.addTask(new Task(getTaskWithName(benson)));
            tdl.addTask(new Task(getTaskWithName(carl)));
            tdl.addTask(new Task(getTaskWithName(daniel)));
            tdl.addTask(new Task(getTaskWithName(elle)));
            tdl.addTask(new Task(getTaskWithName(fiona)));
            tdl.addTask(new Task(getTaskWithName(george)));
        } catch (IllegalValueException e) {
            assert false : "not possible";
        }
    }
    
    public static TestTask getTaskWithName(String name) throws IllegalValueException {
        return new TaskBuilder().withName(name).withUncompletedStatus().build();
    }

    public static TestTask getEventTestTask() throws IllegalValueException {
        return new TaskBuilder().withName("meeting")
                                     .withUncompletedStatus()
                                     .withStartTime(yesterday)
                                     .withEndTime(tomorrow).build();
    }

    public static TestTask getDeadlineTestTask() throws IllegalValueException {
        return new TaskBuilder().withName("due soon")
                                        .withUncompletedStatus()
                                        .withEndTime(tomorrow).build();
    }

    public static TestTask getFloatingTestTask() throws IllegalValueException {
        return new TaskBuilder().withName("anytime").withUncompletedStatus().build();
    }
    
    public TestTask[] getTypicalTasks() throws IllegalValueException {
        return new TestTask[]{getTaskWithName(alice), 
                                getTaskWithName(benson), 
                                getTaskWithName(carl), 
                                getTaskWithName(daniel), 
                                getTaskWithName(elle), 
                                getTaskWithName(fiona), 
                                getTaskWithName(george)};
    }

    public ToDoList getTypicalToDoList(){
        ToDoList tdl = new ToDoList();
        loadToDoListWithSampleData(tdl);
        return tdl;
    }
}
