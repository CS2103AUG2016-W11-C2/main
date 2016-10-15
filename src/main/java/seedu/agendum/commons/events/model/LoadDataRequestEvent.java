package seedu.agendum.commons.events.model;

import seedu.agendum.commons.events.BaseEvent;

/** Indicates the ToDoList in the model has changed*/
public class LoadDataRequestEvent extends BaseEvent {

    public final String loadLocation;

    public LoadDataRequestEvent(String loadLocation){
        this.loadLocation = loadLocation;
    }

    @Override
    public String toString() {
        return "Request to load from: " + loadLocation;
    }
}
