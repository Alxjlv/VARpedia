package models.chunk;

import javafx.beans.property.*;
import models.voice_synthesizer.VoiceSynthesizer;

import java.io.*;

/**
 * Chunk represents a piece of text and an associated {@link VoiceSynthesizer}
 * @author Tait & Alex
 */
public class Chunk implements Externalizable {
    /* Required for Externalizable */
    private static final long serialVersionUID = 3391846346520945615L;

    /**
     * The text property of this chunk
     */
    private ReadOnlyStringWrapper text = new ReadOnlyStringWrapper();

    /**
     * The voice synthesizer property of this chunk
     */
    private ReadOnlyObjectWrapper<VoiceSynthesizer> voiceSynthesizer = new ReadOnlyObjectWrapper<>();

    /**
     * Public default constructor required by {@link Externalizable}. Note: Users should not use this constructor
     */
    public Chunk() {
        this(null, null);
    }

    /**
     * Constructs a Chunk using provided text and voice synthesizer
     * @param text The text for this chunk
     * @param voiceSynthesizer The voice synthesizer for this chunk
     */
    public Chunk(String text, VoiceSynthesizer voiceSynthesizer) {
        setText(text);
        setVoiceSynthesizer(voiceSynthesizer);
    }

    /* Serialize a Chunk */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getText());
        out.writeObject(getVoiceSynthesizer());
    }

    /* Deserialize a chunk */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setText(in.readUTF());
        setVoiceSynthesizer((VoiceSynthesizer) in.readObject());
    }

    /* JavaFX Beans (getter, setter, property) */

    public String getText() {
        return text.get();
    }
    private void setText(String text) {
        this.text.set(text);
    }
    public ReadOnlyStringProperty textProperty() {
        return text.getReadOnlyProperty();
    }

    public VoiceSynthesizer getVoiceSynthesizer() {
        return voiceSynthesizerProperty().get();
    }
    private void setVoiceSynthesizer(VoiceSynthesizer voiceSynthesizer) {
        this.voiceSynthesizer.set(voiceSynthesizer);
    }
    public ReadOnlyObjectProperty<VoiceSynthesizer> voiceSynthesizerProperty() {
        return voiceSynthesizer.getReadOnlyProperty();
    }
}
