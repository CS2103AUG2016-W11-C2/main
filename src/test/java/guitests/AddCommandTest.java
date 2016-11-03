package guitests;

import guitests.guihandles.TaskCardHandle;
import org.junit.Test;
import seedu.agendum.logic.commands.AddCommand;
import seedu.agendum.commons.core.Messages;
import seedu.agendum.testutil.TestTask;
import seedu.agendum.testutil.TestUtil;
import seedu.agendum.testutil.TypicalTestTasks;

import static org.junit.Assert.assertTrue;

public class AddCommandTest extends ToDoListGuiTest {

    @Test
    public void add() {
        //add one task
        TestTask[] currentList = td.getTypicalTasks();
        TestTask taskToAdd = TypicalTestTasks.floatingTask;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add another deadline task
        taskToAdd = TypicalTestTasks.deadlineTask;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add another event task
        taskToAdd = TypicalTestTasks.eventTask;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add duplicate task
        commandBox.runCommand(TypicalTestTasks.floatingTask.getAddCommand());
        assertResultMessage(AddCommand.MESSAGE_DUPLICATE_TASK);
        assertAllPanelsMatch(currentList);

        //add to empty list
        commandBox.runCommand("delete 1-10");
        assertAddSuccess(TypicalTestTasks.floatingTask);

        //invalid command
        commandBox.runCommand("adds Johnny");
        assertResultMessage(String.format(Messages.MESSAGE_UNKNOWN_COMMAND_WITH_SUGGESTION, "add"));
    }

    private void assertAddSuccess(TestTask taskToAdd, TestTask... currentList) {
        commandBox.runCommand(taskToAdd.getAddCommand());

        //confirm the new card contains the right data
        if (!taskToAdd.isCompleted() && !taskToAdd.hasTime()) {
            TaskCardHandle addedCard = floatingTasksPanel.navigateToTask(taskToAdd.getName().fullName);
            assertMatching(taskToAdd, addedCard);
        } else if (!taskToAdd.isCompleted() && taskToAdd.hasTime()) {
            TaskCardHandle addedCard = upcomingTasksPanel.navigateToTask(taskToAdd.getName().fullName);
            assertMatching(taskToAdd, addedCard);
        } else {
            TaskCardHandle addedCard = completedTasksPanel.navigateToTask(taskToAdd.getName().fullName);
            assertMatching(taskToAdd, addedCard);
        }

        //confirm the list now contains all previous tasks plus the new task
        taskToAdd.setLastUpdatedTimeToNow();
        TestTask[] expectedList = TestUtil.addTasksToList(currentList, taskToAdd);
        assertAllPanelsMatch(expectedList);
    }
}
