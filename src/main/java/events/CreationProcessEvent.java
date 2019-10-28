package events;

import java.util.EventObject;

/**
 * This event is responsible for sending back the state the creation process is in to other objects
 * @author Tait & Alex
 */
public class CreationProcessEvent extends EventObject {
    public enum Status {
        BEGIN_CREATE,
        BEGIN_EDIT,
        CANCEL_CREATE,
        CANCEL_EDIT,
        SAVE
    }

    private Status status;

    public CreationProcessEvent(Object source, Status status) {
        super(source);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}