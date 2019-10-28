package models.chunk;

import javafx.beans.property.*;
import models.voice_synthesizer.VoiceSynthesizer;

import java.io.*;

/**
 * A Chunk represents an audio-file containing text-to-speech
 */
public class Chunk implements Externalizable {
    private static final long serialVersionUID = 3391846346520945615L;

    private ReadOnlyStringWrapper text = new ReadOnlyStringWrapper();
    private ReadOnlyObjectWrapper<VoiceSynthesizer> synthesizer = new ReadOnlyObjectWrapper<>();

    public Chunk() {
        this(null, null);
    }

    public Chunk(String text, VoiceSynthesizer voiceSynthesizer) {
        setText(text);
        setSynthesizer(voiceSynthesizer);
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

    public VoiceSynthesizer getSynthesizer() {
        return synthesizerProperty().get();
    }
    private void setSynthesizer(VoiceSynthesizer voiceSynthesizer) {
        this.synthesizer.set(voiceSynthesizer);
    }
    public ReadOnlyObjectProperty<VoiceSynthesizer> synthesizerProperty() {
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
        setSynthesizer((VoiceSynthesizer) in.readObject());
    }
}
