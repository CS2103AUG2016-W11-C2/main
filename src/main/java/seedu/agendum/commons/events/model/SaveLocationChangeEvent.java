package seedu.agendum.commons.events.model;

import seedu.agendum.commons.events.BaseEvent;
//@@author A0148095X
/** Indicates a request from model to change the save location of the data file*/
public class SaveLocationChangeEvent extends BaseEvent {

    public final String location;

    public SaveLocationChangeEvent(String saveLocation){
        this.location = saveLocation;
    }

    @Override
    public String toString() {
        return "Request to change save location to: " + location;
    }
}
