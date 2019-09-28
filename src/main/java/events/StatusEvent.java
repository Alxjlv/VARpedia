package events;

import java.util.EventObject;

public class StatusEvent extends EventObject {
    private boolean status = false;

    public StatusEvent(Object source, boolean success) {
        super(source);
        status = success;
    }

    public boolean getStatus() {
        return status;
    }
}