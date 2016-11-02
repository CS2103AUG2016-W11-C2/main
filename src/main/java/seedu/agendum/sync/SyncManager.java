package seedu.agendum.sync;

import seedu.agendum.commons.core.ComponentManager;
import seedu.agendum.commons.core.LogsCenter;
import seedu.agendum.model.task.Task;

import java.util.logging.Logger;

public class SyncManager extends ComponentManager implements Sync {
    private final Logger logger = LogsCenter.getLogger(SyncManager.class);
    private SyncStatus syncStatus = SyncStatus.NOTRUNNING;

    private final SyncProvider syncProvider;

    public SyncManager(SyncProvider syncProvider) {
        this.syncProvider = syncProvider;
        this.syncProvider.setManager(this);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    @Override
    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public void startSyncing() {
        syncProvider.initialize();
    }

    @Override
    public void stopSyncing() {
        syncProvider.stop();
    }

    @Override
    public void addNewEvent(Task task) {
        if (syncStatus == SyncStatus.RUNNING) {
            if (task.getStartDateTime().isPresent() && task.getEndDateTime().isPresent()) {
                syncProvider.addNewEvent(task);
            }
        }
    }
}
