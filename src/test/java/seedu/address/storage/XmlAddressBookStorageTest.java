package seedu.address.storage;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.ToDoList;
import seedu.address.model.task.Task;
import seedu.address.model.ReadOnlyToDoList;
import seedu.address.testutil.TypicalTestPersons;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class XmlAddressBookStorageTest {
    private static String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/XmlAddressBookStorageTest/");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void readAddressBook_nullFilePath_assertionFailure() throws Exception {
        thrown.expect(AssertionError.class);
        readAddressBook(null);
    }

    private java.util.Optional<ReadOnlyToDoList> readAddressBook(String filePath) throws Exception {
        return new XmlToDoListStorage(filePath).readToDoList(addToTestDataPathIfNotNull(filePath));
    }

    private String addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER + prefsFileInTestDataFolder
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.xml").isPresent());
    }

    @Test
    public void read_notXmlFormat_exceptionThrown() throws Exception {

        thrown.expect(DataConversionException.class);
        readAddressBook("NotXmlFormatAddressBook.xml");

        /* IMPORTANT: Any code below an exception-throwing line (like the one above) will be ignored.
         * That means you should not have more than one exception test in one method
         */
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        String filePath = testFolder.getRoot().getPath() + "TempAddressBook.xml";
        TypicalTestPersons td = new TypicalTestPersons();
        ToDoList original = td.getTypicalAddressBook();
        XmlToDoListStorage xmlAddressBookStorage = new XmlToDoListStorage(filePath);

        //Save in new file and read back
        xmlAddressBookStorage.saveToDoList(original, filePath);
        ReadOnlyToDoList readBack = xmlAddressBookStorage.readToDoList(filePath).get();
        assertEquals(original, new ToDoList(readBack));

        //Modify data, overwrite exiting file, and read back
        original.addTask(new Task(TypicalTestPersons.hoon));
        original.removeTask(new Task(TypicalTestPersons.alice));
        xmlAddressBookStorage.saveToDoList(original, filePath);
        readBack = xmlAddressBookStorage.readToDoList(filePath).get();
        assertEquals(original, new ToDoList(readBack));

        //Save and read without specifying file path
        original.addTask(new Task(TypicalTestPersons.ida));
        xmlAddressBookStorage.saveToDoList(original); //file path not specified
        readBack = xmlAddressBookStorage.readToDoList().get(); //file path not specified
        assertEquals(original, new ToDoList(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_assertionFailure() throws IOException {
        thrown.expect(AssertionError.class);
        saveAddressBook(null, "SomeFile.xml");
    }

    private void saveAddressBook(ReadOnlyToDoList addressBook, String filePath) throws IOException {
        new XmlToDoListStorage(filePath).saveToDoList(addressBook, addToTestDataPathIfNotNull(filePath));
    }

    @Test
    public void saveAddressBook_nullFilePath_assertionFailure() throws IOException {
        thrown.expect(AssertionError.class);
        saveAddressBook(new ToDoList(), null);
    }


}
