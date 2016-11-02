package seedu.agendum.commons.core;

import org.junit.Test;

import seedu.agendum.commons.exceptions.IllegalValueException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

public class ConfigTest {
    
    private Config one;
    private Config another;
    
    @Before
    public void setup() {
        one = new Config();
        another = new Config();
    }
    
    @Test
    public void toString_defaultObject_stringReturned() {        
        StringBuilder sb = new StringBuilder();
        sb.append("App title : Agendum");
        sb.append("\nCurrent log level : INFO");
        sb.append("\nAlias Table file location: " + Config.DEFAULT_ALIAS_TABLE_FILE);
        sb.append("\nPreference file Location : " + Config.DEFAULT_USER_PREFS_FILE);
        sb.append("\nLocal data file location : " + Config.DEFAULT_SAVE_LOCATION);
        sb.append("\nToDoList name : MyToDoList");

        assertEquals(sb.toString(), new Config().toString());
    }

    @Test
    public void equals_nullComparison_returnsFalse() {
        assertFalse(one.equals(null));
    }
    
    @Test
    public void equals_symmetric_returnsTrue() throws IllegalValueException {
        assertTrue(one.equals(another) && another.equals(one));
    }
    
    @Test
    public void hashCode_symmetric_returnsTrue() throws IllegalValueException {
        assertTrue(one.hashCode() == another.hashCode());
    }


}
