package seedu.agendum.commons.events.model;

import seedu.agendum.commons.events.BaseEvent;

/** Indicates the ToDoList in the model has changed*/
public class ChangeSaveLocationRequestEvent extends BaseEvent {

    public final String saveLocation;

    public ChangeSaveLocationRequestEvent(String saveLocation){
        this.saveLocation = saveLocation;
    }

    @Override
    public String toString() {
        return "Request to change save location to: " + saveLocation;
    }
}
