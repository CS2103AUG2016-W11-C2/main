package seedu.agendum.sync;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.matchers.Any;
import seedu.agendum.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.*;
import static seedu.agendum.commons.core.Config.DEFAULT_DATA_DIR;

// @@author A0003878Y
public class SyncProviderGoogleTests {
    private static final File DATA_STORE_CREDENTIAL = new File(DEFAULT_DATA_DIR + "StoredCredential");

    private static final List<File> DATA_STORE_TEST_CREDENTIALS = Arrays.asList(
            new File("cal/StoredCredential_1"),
            new File("cal/StoredCredential_2"),
            new File("cal/StoredCredential_3")
            );

    private SyncProviderGoogle syncProviderGoogle;
    private SyncManager mockSyncManager;

    public SyncProviderGoogleTests() {
        copyTestCredentials();

        mockSyncManager = mock(SyncManager.class);
        syncProviderGoogle = spy(new SyncProviderGoogle());
        syncProviderGoogle.setManager(mockSyncManager);
        syncProviderGoogle.start();
    }

    public static void copyTestCredentials() {
        try {
            DATA_STORE_CREDENTIAL.delete();
            Files.copy(getRandomCredential().toPath(), DATA_STORE_CREDENTIAL.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getRandomCredential() {
        int r = new Random().nextInt(DATA_STORE_TEST_CREDENTIALS.size());
        return DATA_STORE_TEST_CREDENTIALS.get(r);
    }

    @Test
    public void syncProviderGoogle_start_createCalendar() {
        // Verify if Sync Manager's status was changed
        verify(mockSyncManager).setSyncStatus(Sync.SyncStatus.RUNNING);
    }

    @Test
    public void syncProviderGoogle_startIfNeeded_credentialsFound() {
        syncProviderGoogle.startIfNeeded();

        // Verify Sync Provider did start
        verify(syncProviderGoogle, atLeast(2)).start();
    }

    @Test
    public void syncProviderGoogle_startIfNeeded_credentialsNotFound() {
        DATA_STORE_CREDENTIAL.delete();
        syncProviderGoogle.startIfNeeded();

        // Verify Sync Provider should not start
        verify(syncProviderGoogle, atLeastOnce()).start();
    }

    @Test
    public void syncProviderGoogle_addEvent_successful() {
        Task mockTask = mock(Task.class);
        Optional<LocalDateTime> fakeTime = Optional.of(LocalDateTime.now());

        when(mockTask.getStartDateTime()).thenReturn(fakeTime);
        when(mockTask.getEndDateTime()).thenReturn(fakeTime);

        syncProviderGoogle.addNewEvent(mockTask);
    }

    @Test
    public void syncProviderGoogle_deleteEvent_successful() {
        Task mockTask = mock(Task.class);
        Optional<LocalDateTime> fakeTime = Optional.of(LocalDateTime.now());

        when(mockTask.getStartDateTime()).thenReturn(fakeTime);
        when(mockTask.getEndDateTime()).thenReturn(fakeTime);

        syncProviderGoogle.deleteEvent(mockTask);
    }

}
