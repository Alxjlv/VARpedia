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

    private ReadOnlyStringWrapper text;
//    File folder;
    // TODO - Add Synthesizer (Clone/Immutable). Ensure hashCode() updated
    private ReadOnlyObjectWrapper<Synthesizer> synthesizer;

    public Chunk(String text, Synthesizer synthesizer) {
        this.text = new ReadOnlyStringWrapper(text);
        this.synthesizer = new ReadOnlyObjectWrapper<>(synthesizer);
    }

    public Chunk() {
        this(null, null);
    }

    /**
     * Get the text that is spoken
     * @return The text that is spoken
     */
    public String getText() {
        return textProperty().get();
    }

    public ReadOnlyStringProperty textProperty() {
        return text.getReadOnlyProperty();
    }

    public Synthesizer getSynthesizer() {
        return synthesizerProperty().get();
    }

    public ReadOnlyObjectProperty<Synthesizer> synthesizerProperty() {
        return synthesizer.getReadOnlyProperty();
    }

    /**
     * Get the audio file
     * @return The audio file
     */
//    public File getFolder() {
//        return folder;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(text); // TODO - Add synthesizer
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chunk)) {
            return false;
        }
        Chunk c = (Chunk)o;
        return getText().equals(c.getText()); // TODO - Add Synthesizer
//        return getText().equals(c.getText())&&
//                getFolder().getAbsoluteFile().equals(c.getFolder().getAbsoluteFile());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(text.get());
        out.writeObject(synthesizer.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        text.set(in.readUTF());
        synthesizer.set((Synthesizer) in.readObject());
    }
}
