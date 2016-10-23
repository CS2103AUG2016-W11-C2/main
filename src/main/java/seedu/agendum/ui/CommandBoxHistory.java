package seedu.agendum.ui;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Stores previous valid and invalid commands in a linked list with a max size
 * New commands are added to the head of the linked list
 */
public class CommandBoxHistory {
    
    private static final int MAX_PREVIOUS_LINES = 15;
    private LinkedList<String> pastCommands;
    private ListIterator<String> iterator;
    private String lastCommand = "";
    
    public CommandBoxHistory() {
        pastCommands = new LinkedList<String>();
        iterator = pastCommands.listIterator();      
    }

    /**
     * Retrieves the last command entered
     */
    public String getLastCommand() {
        return lastCommand;
    }

    /**
     * Retrieves the previous valid/invalid command
     * before the current command in the command box
     * If there is no previous command, returns an empty string to clear the command box
     */
    public String getPreviousCommand() {
        if (!iterator.hasNext()) {
            return null;
        } else {
            return iterator.next();
        }
    }

    /**
     * Retrieves the previous valid/invalid command
     * after the current command in the command box
     * If there is no new command, return an empty string to clear the command box
     */
    public String getNextCommand() {
        if (!iterator.hasPrevious()) {
            return null;
        } else {
            return iterator.previous();
        }        
    }

    /**
     * Takes in the latest command string entered and add it to command box history
     * Update the iterator to point to the latest element 
     */
    public void saveNewCommand(String newCommand) {
       lastCommand = newCommand;
       pastCommands.addFirst(lastCommand);

       if (pastCommands.size() > MAX_PREVIOUS_LINES) {
           pastCommands.removeLast();
       }

       iterator = pastCommands.listIterator();
    }

}
