package events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class CreateViewEvent extends Event {
    private String next = "/SnippetView.fxml";

    public CreateViewEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public CreateViewEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
    }
}
