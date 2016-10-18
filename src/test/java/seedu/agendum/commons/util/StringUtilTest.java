package seedu.agendum.commons.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void isUnsignedPositiveInteger() {
        assertFalse(StringUtil.isUnsignedInteger(null));
        assertFalse(StringUtil.isUnsignedInteger(""));
        assertFalse(StringUtil.isUnsignedInteger("a"));
        assertFalse(StringUtil.isUnsignedInteger("aaa"));
        assertFalse(StringUtil.isUnsignedInteger("  "));
        assertFalse(StringUtil.isUnsignedInteger("-1"));
        assertFalse(StringUtil.isUnsignedInteger("0"));
        assertFalse(StringUtil.isUnsignedInteger("+1")); //should be unsigned
        assertFalse(StringUtil.isUnsignedInteger("-1")); //should be unsigned
        assertFalse(StringUtil.isUnsignedInteger(" 10")); //should not contain whitespaces
        assertFalse(StringUtil.isUnsignedInteger("10 ")); //should not contain whitespaces
        assertFalse(StringUtil.isUnsignedInteger("1 0")); //should not contain whitespaces

        assertTrue(StringUtil.isUnsignedInteger("1"));
        assertTrue(StringUtil.isUnsignedInteger("10"));
    }

    @Test
    public void getDetails_exceptionGiven(){
        assertThat(StringUtil.getDetails(new FileNotFoundException("file not found")),
                   containsString("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        StringUtil.getDetails(null);
    }
    
    @Test
    public void isValidPathToFile(){
        // non-absolute file paths
        assertFalse(StringUtil.isValidPathToFile(null));
        assertFalse(StringUtil.isValidPathToFile("")); // empty path
        assertFalse(StringUtil.isValidPathToFile("a")); // missing file type
        assertFalse(StringUtil.isValidPathToFile("data/xml")); // missing file name/type
        assertFalse(StringUtil.isValidPathToFile("data/.xml")); // missing file name
        assertFalse(StringUtil.isValidPathToFile("data/ .xml")); // invalid file name
        assertFalse(StringUtil.isValidPathToFile("data /valid.xml")); // invalid folder name with spaces after
        assertFalse(StringUtil.isValidPathToFile(" data/valid.xml")); // invalid folder name with spaces before
        assertFalse(StringUtil.isValidPathToFile("data.xml/data.xml")); // invalid folder name

        assertTrue(StringUtil.isValidPathToFile("a/a.xml"));
        assertTrue(StringUtil.isValidPathToFile("Program Files/data.xml"));
        assertTrue(StringUtil.isValidPathToFile("folder/some-other-folder/data.dat"));
        
        // absolute file paths
        assertFalse(StringUtil.isValidPathToFile("CC:/valid.xml")); // invalid drive
        assertFalse(StringUtil.isValidPathToFile("asd:/valid.xml")); // invalid drive
        assertFalse(StringUtil.isValidPathToFile("C:/")); // missing file name
        assertFalse(StringUtil.isValidPathToFile("C:/Program Files")); // missing file name
        assertFalse(StringUtil.isValidPathToFile("C:/a")); // file name missing type
        assertFalse(StringUtil.isValidPathToFile("C:/data/xml")); // file missing name/type
        assertFalse(StringUtil.isValidPathToFile("C:/data/.xml")); // file missing name
        assertFalse(StringUtil.isValidPathToFile("C:/data/ .xml")); // invalid file name
        assertFalse(StringUtil.isValidPathToFile("C:/data /valid.xml")); // invalid folder name with spaces after
        assertFalse(StringUtil.isValidPathToFile("C:/ data/valid.xml")); // invalid folder name with spaces before
        assertFalse(StringUtil.isValidPathToFile("C:/data.xml/data.xml")); // invalid folder name
        assertFalse(StringUtil.isValidPathToFile("1:/data.xml")); // invalid drive
        assertFalse(StringUtil.isValidPathToFile("/usr/.xml")); // invalid file name - no file name
        assertFalse(StringUtil.isValidPathToFile("/ usr/data.xml")); // invalid folder with spaces before
        assertFalse(StringUtil.isValidPathToFile("/usr /data.xml")); // invalid folder with spaces after

        assertTrue(StringUtil.isValidPathToFile("C:/a/a.xml"));
        assertTrue(StringUtil.isValidPathToFile("C:/Program Files/data.xml"));
        assertTrue(StringUtil.isValidPathToFile("Z:/folder/some-other-folder/data.dat"));
        assertTrue(StringUtil.isValidPathToFile("a:/folder/some-other-folder/data.dat"));
        assertTrue(StringUtil.isValidPathToFile("/usr/bin/data.xml"));
        assertTrue(StringUtil.isValidPathToFile("/home/data.xml"));
        assertTrue(StringUtil.isValidPathToFile("/Users/test/Desktop/data.xml"));
        
    }
    
}
