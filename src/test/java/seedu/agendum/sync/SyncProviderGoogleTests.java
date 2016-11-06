package seedu.agendum.sync;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Any;
import seedu.agendum.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static seedu.agendum.commons.core.Config.DEFAULT_DATA_DIR;

public class SyncProviderGoogleTests {
    private static final File DATA_STORE_CREDENTIAL = new File(DEFAULT_DATA_DIR + "StoredCredential");
    private static final File DATA_STORE_CREDENTIAL_TEST = new File("StoredCredentialForTesting");

    private SyncProviderGoogle syncProviderGoogle;
    private SyncManager mockSyncManager;

    @Before
    public void setUp() {
        try {
            Files.copy(DATA_STORE_CREDENTIAL_TEST.toPath(), DATA_STORE_CREDENTIAL.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockSyncManager = mock(SyncManager.class);
        syncProviderGoogle = spy(new SyncProviderGoogle());
        syncProviderGoogle.setManager(mockSyncManager);
    }

    @After
    public void tearDown() {
        syncProviderGoogle.stop();
    }

    @Test
    public void syncProviderGoogle_start_calendarAlreadyExists() {
        syncProviderGoogle.deleteAgendumCalendar();
        syncProviderGoogle.start();

        verify(mockSyncManager).setSyncStatus(Sync.SyncStatus.RUNNING);
    }

    @Test
    public void syncProviderGoogle_start_createCalendar() {
        syncProviderGoogle.start();

        verify(mockSyncManager).setSyncStatus(Sync.SyncStatus.RUNNING);
    }

    @Test
    public void syncProviderGoogle_startIfNeeded_credentialsFound() {
        syncProviderGoogle.startIfNeeded();
        verify(syncProviderGoogle).start();
    }

    @Test
    public void syncProviderGoogle_startIfNeeded_credentialsNotFound() {
        DATA_STORE_CREDENTIAL.delete();
        syncProviderGoogle.startIfNeeded();
        verify(syncProviderGoogle, never()).start();
    }

    @Test
    public void syncProviderGoogle_addEvent_successful() {
        syncProviderGoogle.start();

        Task mockTask = mock(Task.class);
        Optional<LocalDateTime> fakeTime = Optional.of(LocalDateTime.now());

        when(mockTask.getStartDateTime()).thenReturn(fakeTime);
        when(mockTask.getEndDateTime()).thenReturn(fakeTime);

        syncProviderGoogle.addNewEvent(mockTask);
    }

    @Test
    public void syncProviderGoogle_deleteEvent_successful() {
        syncProviderGoogle.start();

        Task mockTask = mock(Task.class);
        Optional<LocalDateTime> fakeTime = Optional.of(LocalDateTime.now());

        when(mockTask.getStartDateTime()).thenReturn(fakeTime);
        when(mockTask.getEndDateTime()).thenReturn(fakeTime);

        syncProviderGoogle.deleteEvent(mockTask);
    }

}
