package events;

import java.util.EventObject;

public class CreationProcessEvent extends EventObject {
    public enum Status {
        BEGIN,
        SAVE_EDIT,
        SAVE_CREATE,
        CANCEL_CREATE,
        CANCEL_EDIT;
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