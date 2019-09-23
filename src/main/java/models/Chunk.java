package models;

import java.io.File;

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
}
