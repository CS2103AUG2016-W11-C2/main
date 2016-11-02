package seedu.agendum.testutil;

import java.time.LocalDateTime;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.ToDoList;
import seedu.agendum.model.task.*;

/**
 *
 */
public class TypicalTestTasks {

    public static TestTask alice, benson, carl, daniel, elle, fiona, george, hoon, ida,
                           floatingTask, deadlineTask, eventTask;
    
    private LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    private LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    public TypicalTestTasks() {
        try {
            alice =  new TaskBuilder().withName("meet Alice Pauline").withUncompletedStatus().build();
            benson = new TaskBuilder().withName("meet Benson Meier").withUncompletedStatus().build();
            carl = new TaskBuilder().withName("meet Carl Kurz").withUncompletedStatus().build();
            daniel = new TaskBuilder().withName("meet Daniel Meier").withUncompletedStatus().build();
            elle = new TaskBuilder().withName("meet Elle Meyer").withUncompletedStatus().build();
            fiona = new TaskBuilder().withName("meet Fiona Kunz").withUncompletedStatus().build();
            george = new TaskBuilder().withName("meet George Best").withUncompletedStatus().build();

            //Manually added
            hoon = new TaskBuilder().withName("meet Hoon Meier").withUncompletedStatus().build();
            ida = new TaskBuilder().withName("meet Ida Mueller").withUncompletedStatus().build();
            
            floatingTask = new TaskBuilder().withName("anytime").withUncompletedStatus().build();
            deadlineTask = new TaskBuilder().withName("due soon")
                                            .withUncompletedStatus()
                                            .withEndTime(tomorrow).build();
            eventTask = new TaskBuilder().withName("meeting")
                                         .withUncompletedStatus()
                                         .withStartTime(yesterday)
                                         .withEndTime(tomorrow).build();
        
        } catch (IllegalValueException e) {
            e.printStackTrace();
            assert false : "not possible";
        }
    }

    public static void loadToDoListWithSampleData(ToDoList tdl) {

        try {
            tdl.addTask(new Task(alice));
            tdl.addTask(new Task(benson));
            tdl.addTask(new Task(carl));
            tdl.addTask(new Task(daniel));
            tdl.addTask(new Task(elle));
            tdl.addTask(new Task(fiona));
            tdl.addTask(new Task(george));
        } catch (UniqueTaskList.DuplicateTaskException e) {
            assert false : "not possible";
        }
    }

    public TestTask[] getTypicalTasks() {
        return new TestTask[]{alice, benson, carl, daniel, elle, fiona, george};
    }

    public ToDoList getTypicalToDoList(){
        ToDoList tdl = new ToDoList();
        loadToDoListWithSampleData(tdl);
        return tdl;
    }
}
