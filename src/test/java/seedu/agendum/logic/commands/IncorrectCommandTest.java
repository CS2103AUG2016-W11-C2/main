package seedu.agendum.logic.commands;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by vprem on 6/11/16.
 */
public class IncorrectCommandTest {
    @Test
    public void getName()  {
        assertNull(IncorrectCommand.getName());
    }

    @Test
    public void getFormat()  {
        assertNull(IncorrectCommand.getFormat());
    }

    @Test
    public void getDescription()  {
        assertNull(IncorrectCommand.getDescription());
    }

}