package events;

import models.Creation;

import java.util.EventObject;

public class NewCreationEvent extends EventObject {
    private Creation creation;

    public NewCreationEvent(Object source, Creation creation) {
        super(source);
        this.creation = creation;
    }

    public Creation getCreation() {
        return creation;
    }
}
