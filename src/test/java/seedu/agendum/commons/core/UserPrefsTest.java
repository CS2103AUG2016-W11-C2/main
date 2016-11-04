package seedu.agendum.commons.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import seedu.agendum.model.UserPrefs;

public class UserPrefsTest {

    private UserPrefs one, another;
    
    @Before
    public void setUp() {
        one = new UserPrefs();
        another = new UserPrefs();
    }
    
    @Test
    public void equals_differentObject_returnsFalse() {
        assertFalse(one.equals(new Object()));
    }
    
    @Test
    public void equals_symmetric_returnsTrue() {
        // equals to itself and object with same parameters
        assertTrue(one.equals(one));
        assertTrue(one.equals(another));        
    }
    
    @Test
    public void hashcode_symmetric_returnsTrue() {
        assertEquals(one.hashCode(), another.hashCode());
    }    
    
}
