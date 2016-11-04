package seedu.agendum;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import seedu.agendum.commons.core.Config;
import seedu.agendum.commons.util.ConfigUtil;
import seedu.agendum.model.UserPrefs;
import seedu.agendum.storage.JsonUserPrefsStorage;
import seedu.agendum.storage.StorageManager;
import seedu.agendum.testutil.TestUtil;

//@@author A0148095X
public class MainAppTest {

    private MainApp mainApp;

    private Config defaultConfig;

    private UserPrefs defaultUserPrefs;

    private final String pathToBadConfig = TestUtil.getFilePathInSandboxFolder("bad_config.json");
    private final String pathToReadOnlyConfig = TestUtil.getFilePathInSandboxFolder("read_only_config.json");

    private final String pathToBadUserPrefs = TestUtil.getFilePathInSandboxFolder("bad_user_prefs.json");
    private final String pathToReadOnlyUserPrefs = TestUtil.getFilePathInSandboxFolder("read_only_user_prefs.json");

    @Before
    public void setUp() {
        mainApp = new MainApp();

        defaultConfig = new Config();

        defaultUserPrefs = new UserPrefs();

        createEmptyFile(pathToBadConfig);
        createReadOnlyConfigFile();

        createEmptyFile(pathToBadUserPrefs);
        createReadOnlyUserPrefsFile();
    }

    @Test
    public void initConfig_nullFilePath_returnsDefaultConfig() {
        Config config = mainApp.initConfig(null);
        assertEquals(config, defaultConfig);
    }

    @Test
    public void initConfig_validFilePathButInvalidFileFormat_returnsDefaultConfig() {
        Config config = mainApp.initConfig(pathToBadConfig);
        assertEquals(config, defaultConfig);
    }

    @Test
    public void initConfig_validFilePathValidFormatButUnwriteable_returnsDefaultConfigLogsWarning() {
        Config config = mainApp.initConfig(pathToReadOnlyConfig);
        assertEquals(config, defaultConfig);
    }

    @Test
    public void initPrefs_invalidUserPrefsFileFormat_returnsDefaultUserPrefs() {
        // Set up a config to point to bad user prefs
        Config config = new Config();
        config.setUserPrefsFilePath(pathToBadUserPrefs);
        mainApp.storage = new CustomUserPrefsStorageManagerStub(pathToBadUserPrefs);

        UserPrefs userPrefs = mainApp.initPrefs(config);
        assertEquals(userPrefs, defaultUserPrefs);
        
        mainApp.storage = null;
    }

    @Test
    public void initPrefs_validUserPrefsFileFormatButUnwriteable_returnsDefaultUserPrefsLogsWarning() {
        // Set up a config to point to read only prefs
        Config config = new Config();
        config.setUserPrefsFilePath(pathToReadOnlyUserPrefs);
        mainApp.storage = new CustomUserPrefsStorageManagerStub(pathToReadOnlyUserPrefs);

        UserPrefs userPrefs = mainApp.initPrefs(config);
        assertEquals(userPrefs, defaultUserPrefs);
        
        mainApp.storage = null;
    }

    @Test
    public void initPrefs_exceptionThrowingStorage_returnsDefaultUserPrefsLogsWarning() {
        mainApp.storage = new ExceptionThrowingStorageManagerStub();
        
        UserPrefs userPrefs = mainApp.initPrefs(defaultConfig);
        assertEquals(userPrefs, defaultUserPrefs);
        
        mainApp.storage = null;
    }

    private void createEmptyFile(String filePath) {
        File file = new File(filePath);

        // Ensure that the file is empty
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating empty file at: " + filePath);
            System.exit(1);
        }
    }

    private void createReadOnlyConfigFile() {
        File file = new File(pathToReadOnlyConfig);

        // Ensure that the file is empty
        if (file.exists()) {
            file.delete();
        }

        try {
            ConfigUtil.saveConfig(defaultConfig, pathToReadOnlyConfig);
        } catch (IOException e) {
            System.out.println("Error creating read only config file");
            System.exit(1);
        }

        if (!file.setReadOnly()) {
            System.out.println("Unable to set read only config to read only");
            System.exit(1);
        }
    }

    private void createReadOnlyUserPrefsFile() {
        File file = new File(pathToReadOnlyUserPrefs);

        // Ensure that the file is empty
        if (file.exists()) {
            file.delete();
        }

        try {
            JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(pathToReadOnlyUserPrefs);
            userPrefsStorage.saveUserPrefs(defaultUserPrefs, pathToReadOnlyUserPrefs);
        } catch (IOException e) {
            System.out.println("Error creating read only user prefs file");
            System.exit(1);
        }

        if (!file.setReadOnly()) {
            System.out.println("Unable to set read only user prefs to read only");
            System.exit(1);
        }
    }

    class CustomUserPrefsStorageManagerStub extends StorageManager {
        
        public CustomUserPrefsStorageManagerStub(String userPrefsPath) {
            super("", "", userPrefsPath, null);
        }
    }
    
    class ExceptionThrowingStorageManagerStub extends StorageManager {

        public ExceptionThrowingStorageManagerStub() {
            super("","","",null);
        }
        
        @Override
        public Optional<UserPrefs> readUserPrefs() throws IOException {
            throw new IOException("ExceptionThrowingStorageManagerStub: IOException");
        }
    }
}
