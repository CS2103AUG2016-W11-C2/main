package seedu.agendum.storage;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.*;

import javax.xml.bind.annotation.XmlElement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JAXB-friendly version of the Task.
 */
public class XmlAdaptedTask {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String isCompleted;
    @XmlElement(required = true)
    private String isRecurring;
    @XmlElement(required = true)
    private String isChild;
    @XmlElement(required = true)
    private String lastUpdatedTime;
    @XmlElement(required = false)
    private String startDateTime;
    @XmlElement(required = false)
    private String endDateTime;
    @XmlElement(required = false)
    private String period;
    @XmlElement(required = false)
    private String parentName;

    /**
     * No-arg constructor for JAXB use.
     */
    public XmlAdaptedTask() {}


    /**
     * Converts a given Task into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedTask
     */
    public XmlAdaptedTask(ReadOnlyTask source) {
        name = source.getName().fullName;
        isRecurring = Boolean.toString(source.isRecurring());
        isCompleted = Boolean.toString(source.isCompleted());

        isChild = Boolean.toString(source.isChild());
        
        lastUpdatedTime = source.getLastUpdatedTime().format(formatter);

        if (source.getStartDateTime().isPresent()) {
            startDateTime = source.getStartDateTime().get().format(formatter);
        }

        if (source.getEndDateTime().isPresent()) {
            endDateTime = source.getEndDateTime().get().format(formatter);
        }
        if(source.getPeriod() != null) {
            period = source.getPeriod();
        }
        if(source.getParent() != null) {
            parentName = source.getParent().getName().fullName;
        }
    }

    /**
     * Converts this jaxb-friendly adapted task object into the model's Task object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted task
     */
    public Task toTaskModelType() throws IllegalValueException {
        final Name name = new Name(this.name);
        final boolean markedAsCompleted = Boolean.valueOf(isCompleted);

        Task newTask = new Task(name);
        newTask.setLastUpdatedTime(LocalDateTime.parse(this.lastUpdatedTime, formatter));

        if (markedAsCompleted) {
            newTask.markAsCompleted();
        }

        if (startDateTime != null) {
            newTask.setStartDateTime(Optional.ofNullable(LocalDateTime.parse(this.startDateTime, formatter)));
        }

        if (endDateTime != null) {
            newTask.setEndDateTime(Optional.ofNullable(LocalDateTime.parse(this.endDateTime, formatter)));
        }

        return newTask;
    }
    
    /**
     * Converts this jaxb-friendly adapted task object into the model's RecurringTask object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted task
     */
    public RecurringTask toRecurringTaskModelType() throws IllegalValueException {
        final Name name = new Name(this.name);
        
        RecurringTask newRecurringTask = null;
        
        if (startDateTime != null) {
            newRecurringTask = new RecurringTask(name, Optional.ofNullable(LocalDateTime.parse(this.startDateTime, formatter)), 
                    Optional.ofNullable(LocalDateTime.parse(this.endDateTime, formatter)), period);
        } else {
            newRecurringTask = new RecurringTask(name, Optional.ofNullable(LocalDateTime.parse(this.endDateTime, formatter)), period);
        }
        return newRecurringTask;
    }
    
    /**
     * Converts this jaxb-friendly adapted task object into the model's ChildRecurringTask object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted task
     */
    public ChildRecurringTask toChildRecurringTaskModelType(RecurringTask parentRecurringTask) throws IllegalValueException {
        ChildRecurringTask newChildRecurringTask = new ChildRecurringTask(parentRecurringTask);
        if(this.startDateTime != null) {
            newChildRecurringTask.setStartDateTime(Optional.ofNullable(LocalDateTime.parse(this.startDateTime, formatter)));
        }
        newChildRecurringTask.setEndDateTime(Optional.ofNullable(LocalDateTime.parse(this.endDateTime, formatter)));
        return newChildRecurringTask;
    }
    
    public boolean isRecurring() {
        return Boolean.valueOf(isRecurring);
    }
    
    public boolean isChild() {
        return Boolean.valueOf(isChild);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getParentName() {
        return this.parentName;
    }
    
}
