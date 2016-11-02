package seedu.agendum.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.Name;

public class NameTest {
    private String invalidNameString = "Vishnu \n Rachael \n Weigang";
    private String validNameString = "Justin";
    
    @Test
    public void equals_Symmetric_returnsTrue() throws IllegalValueException {
        Name one = new Name(validNameString);  // equals and hashCode check name field value
        Name another = new Name(validNameString);
        assertTrue(one.equals(another) && another.equals(one));
        assertTrue(one.hashCode() == another.hashCode());
    }
    
    @SuppressWarnings("unused")
    @Test (expected = IllegalValueException.class)
    public void name_invalid_throwsIllegalValueException() throws IllegalValueException {
        Name name = new Name(invalidNameString);
    }
}
