package seedu.agendum.storage;

import seedu.agendum.commons.exceptions.IllegalValueException;
import seedu.agendum.model.task.ReadOnlyTask;
import seedu.agendum.model.task.RecurringTask;
import seedu.agendum.model.task.UniqueTaskList;
import seedu.agendum.model.ReadOnlyToDoList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An Immutable ToDoList that is serializable to XML format
 */
@XmlRootElement(name = "todolist")
public class XmlSerializableToDoList implements ReadOnlyToDoList {

    
    @XmlElement
    private List<XmlAdaptedTask> tasks;

    {
        tasks = new ArrayList<>();
    }

    /**
     * Empty constructor required for marshalling
     */
    public XmlSerializableToDoList() {}

    /**
     * Conversion
     */
    public XmlSerializableToDoList(ReadOnlyToDoList src) {
        tasks.addAll(src.getTaskList().stream().map(XmlAdaptedTask::new).collect(Collectors.toList()));
    }
    
    

    //@@author A0148031R
    @Override
    public UniqueTaskList getUniqueTaskList() {
        UniqueTaskList lists = new UniqueTaskList();
        List<XmlAdaptedTask> childrenWatingList = new ArrayList<XmlAdaptedTask>();
        HashMap<String, RecurringTask> parents = new HashMap<String, RecurringTask>();
        try {
            for (XmlAdaptedTask p : tasks) {
                if (p.isRecurring() && p.isChild()) {
                    childrenWatingList.add(p);
                } else if (p.isRecurring()) {
                    RecurringTask parent = p.toRecurringTaskModelType();
                    lists.add(parent);
                    parents.put(parent.getName().fullName, parent);
                } else {
                    lists.add(p.toTaskModelType());
                }
            }
            for (XmlAdaptedTask p : childrenWatingList) {
                if(parents.containsKey(p.getName())) {
                    lists.add(p.toChildRecurringTaskModelType(parents.get(p.getName())));
                } else {
                    lists.add(p.toTaskModelType());
                }
            }
        } catch (IllegalValueException e) {
            // TODO: better error handling
        }
        return lists;
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        List<ReadOnlyTask> tasks = new ArrayList<ReadOnlyTask>();
        Iterator itr =  getUniqueTaskList().iterator();
        while(itr.hasNext()) {
            tasks.add((ReadOnlyTask)itr.next());
        }
        return tasks;
    }
}
