package events;

import java.util.EventObject;

public class CreationProcessEvent extends EventObject {
    public enum Status {
        BEGIN,
        CANCEL,
        CREATE;
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