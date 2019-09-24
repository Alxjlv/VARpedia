package models;

import java.io.File;

public class ChunkBuilder implements Builder<Chunk> {
    private static int id = 0; // TODO - Move to chunk manager?

    private String text;
    private File audioFile; // TODO - Not needed as field? Only used in build()
    private Synthesizer synthesizer;

    public ChunkBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public ChunkBuilder setSynthesizer(Synthesizer synthesizer) {
        this.synthesizer = synthesizer;
        return this;
    }

    @Override
    public Chunk build() {
        // Validate fields

        // TODO Get & validate audio file path
        audioFile = new File(String.format("/chunks/chunk%d.mp3", id)); // TODO - Set /chunks/ as constant elsewhere

        // Create audio file using Synthesizer's Process
        synthesizer.save(text, audioFile);

        // If all good:
        id++;
        return new Chunk(text, audioFile);
    }
}
