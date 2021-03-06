package guitests;

import org.junit.Test;

import seedu.agendum.commons.core.Messages;
import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.logic.commands.RenameCommand;
import seedu.agendum.model.task.Name;
import seedu.agendum.testutil.TestTask;
import seedu.agendum.testutil.TestUtil;

public class RenameCommandTest extends ToDoListGuiTest {

    @Test
    public void rename() throws IllegalValueException {

        //rename the first in the list
        TestTask[] currentList = td.getTypicalTasks();
        int targetIndex = 1;
        assertRenameSuccess(targetIndex, currentList);

        //rename the last in the list
        targetIndex = currentList.length;
        assertRenameSuccess(targetIndex, currentList);

        //rename task in the middle of the list
        targetIndex = currentList.length/2;
        assertRenameSuccess(targetIndex, currentList);

        //invalid index
        commandBox.runCommand("rename " + currentList.length + 1 + " new task name");
        assertResultMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);

        //duplicate task
        commandBox.runCommand("rename " + currentList.length + " " + currentList[targetIndex].getName().toString());
        assertResultMessage(Messages.MESSAGE_DUPLICATE_TASK);
        assertAllPanelsMatch(currentList);
    }

    /**
     * Runs the rename command to rename the task at specified index and confirms the result is correct.
     * @param targetIndexOneIndexed e.g. to rename the first task in the list, 1 should be given as the target index.
     * @param currentList - list of tasks (before renaming).
     */
    private void assertRenameSuccess(int targetIndexOneIndexed, TestTask[] currentList) {
        TestTask taskToRename = currentList[targetIndexOneIndexed - 1]; //-1 because array uses zero indexing
        String newTaskName = taskToRename.getName().toString() + " renamed";

        try {
            TestTask renamedTask = new TestTask(taskToRename);
            renamedTask.setName(new Name(newTaskName));
            TestTask[] expectedList = TestUtil.replaceTaskFromList(currentList, renamedTask, targetIndexOneIndexed - 1);

            commandBox.runCommand("rename " + targetIndexOneIndexed + " " + newTaskName);

            //confirm the list now contains all previous tasks with the specified task's name updated
            assertAllPanelsMatch(expectedList);

            //confirm the result message is correct
            assertResultMessage(String.format(RenameCommand.MESSAGE_SUCCESS, newTaskName));
            
        } catch (IllegalValueException e) {
            e.printStackTrace();
            assert false : "not possible";
        }
    }

}

