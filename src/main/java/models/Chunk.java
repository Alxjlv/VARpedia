package models;

import java.io.File;
import java.util.Objects;

public class Chunk {
    String text;
    File audioFile;

    public Chunk(String text, File audioFile) {
        this.text = text;
        this.audioFile = audioFile;
    }

    public String getText() {
        return text;
    }

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
