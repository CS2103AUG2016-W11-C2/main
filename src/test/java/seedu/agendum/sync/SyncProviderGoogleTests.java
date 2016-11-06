package seedu.agendum.sync;

import org.junit.Before;
import org.junit.Test;
import seedu.agendum.model.task.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static seedu.agendum.commons.core.Config.DEFAULT_DATA_DIR;

public class SyncProviderGoogleTests {
    private SyncProviderGoogle syncProviderGoogle;
    private static final File DATA_STORE_CREDENTIAL = new File(DEFAULT_DATA_DIR + "StoredCredential");

    @Before
    public void setUp() {
        syncProviderGoogle = new SyncProviderGoogle();
    }

    @Test
    public void addevent() {
        Task mockTask = mock(Task.class);
        Optional<LocalDateTime> fakeTime = Optional.of(LocalDateTime.now());

        when(mockTask.getStartDateTime()).thenReturn(fakeTime);
        when(mockTask.getEndDateTime()).thenReturn(fakeTime);

        syncProviderGoogle.addNewEvent(mockTask);
    }

    @Test
    public void deleteEvent() {
        Task mockTask = mock(Task.class);
        Optional<LocalDateTime> fakeTime = Optional.of(LocalDateTime.now());

        when(mockTask.getStartDateTime()).thenReturn(fakeTime);
        when(mockTask.getEndDateTime()).thenReturn(fakeTime);

        syncProviderGoogle.deleteEvent(mockTask);
    }

}
