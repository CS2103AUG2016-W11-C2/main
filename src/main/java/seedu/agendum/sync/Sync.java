package seedu.agendum.sync;

import seedu.agendum.model.task.Task;

//@@author A0003878Y
public interface Sync {

    enum SyncStatus {
        RUNNING, NOTRUNNING
    }

    SyncStatus getSyncStatus();
    void setSyncStatus(SyncStatus syncStatus);
    void startSyncing();
    void stopSyncing();

    void addNewEvent(Task task);
    void deleteEvent(Task task);
}
