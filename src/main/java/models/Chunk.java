package models;

import java.io.File;
import java.util.Objects;

/**
 * A Chunk represents an audio-file containing text-to-speech
 */
public class Chunk {
    String text;
    File folder;
    // TODO - Add Synthesizer (Clone/Immutable). Ensure hashCode() updated

    public Chunk(String text, File folder) {
        this.text = text;
        this.folder = folder;
    }

    /**
     * Get the text that is spoken
     * @return The text that is spoken
     */
    public String getText() {
        return text;
    }

    /**
     * Get the audio file
     * @return The audio file
     */
    public File getFolder() {
        return folder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, folder);
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
        return getText().equals(c.getText())&&
                getFolder().getAbsoluteFile().equals(c.getFolder().getAbsoluteFile());
    }
}
