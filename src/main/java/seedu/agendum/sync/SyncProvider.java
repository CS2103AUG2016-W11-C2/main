package seedu.agendum.sync;

import seedu.agendum.model.task.Task;

//@@author A0003878Y
public abstract class SyncProvider {
    protected Sync syncManager;

    public abstract void initialize();
    public abstract void stop();
    public abstract void addNewEvent(Task task);

    public void setManager(Sync syncManager) {
        this.syncManager = syncManager;
    }
}
