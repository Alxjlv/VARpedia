package models;

import java.io.File;
import java.util.Objects;

/**
 * A Chunk represents an audio-file containing text-to-speech
 */
public class Chunk {
    String text;
    File audioFile;
    // TODO - Add Synthesizer (Clone/Immutable). Ensure hashCode() updated

    public Chunk(String text, File audioFile) {
        this.text = text;
        this.audioFile = audioFile;
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
    public File getAudioFile() {
        return audioFile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, audioFile);
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
                getAudioFile().getAbsoluteFile().equals(c.getAudioFile().getAbsoluteFile());
    }
}
