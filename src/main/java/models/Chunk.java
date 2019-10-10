package models;

import java.io.*;
import java.util.Objects;

/**
 * A Chunk represents an audio-file containing text-to-speech
 */
public class Chunk implements Externalizable {
    private static final long serialVersionUID = 3391846346520945615L;

    private String text;
//    File folder;
    // TODO - Add Synthesizer (Clone/Immutable). Ensure hashCode() updated
    private Synthesizer synthesizer;

    public Chunk(String text, Synthesizer synthesizer) {
        this.text = text;
        this.synthesizer = synthesizer;
    }

    public Chunk() {
    }

    /**
     * Get the text that is spoken
     * @return The text that is spoken
     */
    public String getText() {
        return text;
    }

    public Synthesizer getSynthesizer() {
        return synthesizer;
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
        out.writeUTF(text);
        out.writeObject(synthesizer);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        text = in.readUTF();
        synthesizer = (Synthesizer) in.readObject();
    }
}
