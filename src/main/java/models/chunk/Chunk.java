package models.chunk;

import javafx.beans.property.*;
import models.synthesizer.Synthesizer;

import java.io.*;
import java.util.Objects;

/**
 * A Chunk represents an audio-file containing text-to-speech
 */
public class Chunk implements Externalizable {
    private static final long serialVersionUID = 3391846346520945615L;

    private ReadOnlyStringWrapper text = new ReadOnlyStringWrapper();
    private ReadOnlyObjectWrapper<Synthesizer> synthesizer = new ReadOnlyObjectWrapper<>();

    public Chunk() {
        this(null, null);
    }

    public Chunk(String text, Synthesizer synthesizer) {
        setText(text);
        setSynthesizer(synthesizer);
    }

    public String getText() {
        return text.get();
    }
    private void setText(String text) {
        this.text.set(text);
    }
    public ReadOnlyStringProperty textProperty() {
        return text.getReadOnlyProperty();
    }

    public Synthesizer getSynthesizer() {
        return synthesizerProperty().get();
    }
    private void setSynthesizer(Synthesizer synthesizer) {
        this.synthesizer.set(synthesizer);
    }
    public ReadOnlyObjectProperty<Synthesizer> synthesizerProperty() {
        return synthesizer.getReadOnlyProperty();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getText());
        out.writeObject(getSynthesizer());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setText(in.readUTF());
        setSynthesizer((Synthesizer) in.readObject());
    }
}
