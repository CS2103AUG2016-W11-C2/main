package seedu.agendum.storage;


import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import seedu.agendum.commons.events.model.ChangeSaveLocationEvent;
import seedu.agendum.commons.events.model.LoadDataRequestEvent;
import seedu.agendum.commons.events.model.ToDoListChangedEvent;
import seedu.agendum.commons.events.storage.DataLoadingExceptionEvent;
import seedu.agendum.commons.events.storage.DataSavingExceptionEvent;
import seedu.agendum.commons.exceptions.DataConversionException;
import seedu.agendum.commons.exceptions.FileDeletionException;
import seedu.agendum.commons.util.FileUtil;
import seedu.agendum.model.ReadOnlyToDoList;
import seedu.agendum.model.ToDoList;
import seedu.agendum.model.UserPrefs;
import seedu.agendum.testutil.EventsCollector;
import seedu.agendum.testutil.TestUtil;
import seedu.agendum.testutil.TypicalTestTasks;

public class StorageManagerTest {

    private StorageManager storageManager;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        storageManager = new StorageManager(getTempFilePath("ab"), getTempFilePath("command"),
                getTempFilePath("prefs"), TestUtil.createTempConfig());
    }


    private String getTempFilePath(String fileName) {
        return testFolder.getRoot().getPath() + fileName;
    }


    /*
     * Note: This is an integration test that verifies the StorageManager is properly wired to the
     * {@link JsonUserPrefsStorage} class.
     * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
     */

    @Test
    public void prefsReadSave() throws Exception {
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(300, 600, 4, 6);
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }
    
    /**
     * Verifies that StorageManager is properly wired to {@link JsonAliasTableStorage} class
     */
    @Test
    public void aliasTableReadSave() throws Exception {
        Hashtable<String, String> testingTable = new Hashtable<String, String>();
        testingTable.put("a", "add");
        testingTable.put("d", "delete");
        storageManager.saveAliasTable(testingTable);
        Hashtable<String, String> retrieved = storageManager.readAliasTable().get();
        assertEquals(testingTable, retrieved); 
    }

    @Test
    public void toDoListReadSave() throws Exception {
        ToDoList original = new TypicalTestTasks().getTypicalToDoList();
        storageManager.saveToDoList(original);
        ReadOnlyToDoList retrieved = storageManager.readToDoList().get();
        assertEquals(original, new ToDoList(retrieved));
        //More extensive testing of ToDoList saving/reading is done in XmlToDoListStorageTest
    }

    @Test
    public void getToDoListFilePath(){
        assertNotNull(storageManager.getToDoListFilePath());
    }

    @Test
    public void handleToDoListChangedEventExceptionThrownEventRaised() throws IOException {
        //Create a StorageManager while injecting a stub that throws an exception when the save method is called
        Storage storage = new StorageManager(new XmlToDoListStorageExceptionThrowingStub("dummy"), 
                new JsonAliasTableStorage("dummy"), new JsonUserPrefsStorage("dummy"), TestUtil.createTempConfig());
        EventsCollector eventCollector = new EventsCollector();
        storage.handleToDoListChangedEvent(new ToDoListChangedEvent(new ToDoList()));
        assertTrue(eventCollector.get(0) instanceof DataSavingExceptionEvent);
    }

    //@@author A0148095X
    @Test
    public void handleSaveLocationChangedEvent_validFilePath_success() {
        String validPath = "data/test.xml";
        storageManager.handleChangeSaveLocationEvent(new ChangeSaveLocationEvent(validPath));
        assertEquals(storageManager.getToDoListFilePath(), validPath);
    }
    
    @Test
    public void handleLoadDataRequestEvent_validPathToFileInvalidFile_throwsException() throws IOException, FileDeletionException {
        EventsCollector eventCollector = new EventsCollector();
        String validPath = "data/testLoad.xml";
        assert !FileUtil.isFileExists(validPath);
        
        // File does not exist
        storageManager.handleLoadDataRequestEvent(new LoadDataRequestEvent(validPath));
        DataLoadingExceptionEvent dlee = (DataLoadingExceptionEvent)eventCollector.get(0);
        assertTrue(dlee.exception instanceof NoSuchElementException);

        // File in wrong format
        FileUtil.createFile(new File(validPath));
        storageManager.handleLoadDataRequestEvent(new LoadDataRequestEvent(validPath));
        dlee = (DataLoadingExceptionEvent)eventCollector.get(1);
        assertTrue(dlee.exception instanceof DataConversionException);
        FileUtil.deleteFile(validPath);
    }

    @Test(expected = AssertionError.class)
    public void setToDoListFilePath_nullPath_fail() {
        // null
        storageManager.setToDoListFilePath(null);
    }

    @Test(expected = AssertionError.class)
    public void setToDoListFilePath_pathEmpty_fail() {
        // empty string
        storageManager.setToDoListFilePath("");
    }

    @Test(expected = AssertionError.class)
    public void setToDoListFilePath_pathInvalid_fail() {
        // invalid file path
        storageManager.setToDoListFilePath("1:/.xml");
    }

    public void setToDoListFilePath_pathValid_success() {
        // valid file path
        String validPath = "test/test.xml";
        storageManager.setToDoListFilePath(validPath);
        assertEquals(validPath, storageManager.getToDoListFilePath());
    }
    //@@author

    /**
     * A Stub class to throw an exception when the save method is called
     */
    class XmlToDoListStorageExceptionThrowingStub extends XmlToDoListStorage{

        public XmlToDoListStorageExceptionThrowingStub(String filePath) {
            super(filePath);
        }

        @Override
        public void saveToDoList(ReadOnlyToDoList toDoList, String filePath) throws IOException {
            throw new IOException("dummy exception");
        }
    }


}
