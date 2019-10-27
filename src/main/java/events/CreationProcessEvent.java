package events;

import java.util.EventObject;

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