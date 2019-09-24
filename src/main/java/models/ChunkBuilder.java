package models;

import java.io.File;

/**
 * Implements a {@link Builder} for {@link Chunk} objects
 */
public class ChunkBuilder implements Builder<Chunk> {
    private int id;
    private String text;
//    private File audioFile; // TODO - Not needed as field? Only used in build()
    private Synthesizer synthesizer;

    /**
     * Set the id for the chunk to be built. This method is package-private. Only intended to be called by {@link Manager}
     * @param id The id for the chunk to be built
     * @return {@code this}
     */
    ChunkBuilder setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Set the text to be spoken for the chunk to be built
     * @param text The text to be spoken
     * @return {@code this}
     */
    public ChunkBuilder setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Set the {@link Synthesizer} for the chunk to be built
     * @param synthesizer The Synthesizer for the chunk
     * @return {@code this}
     */
    public ChunkBuilder setSynthesizer(Synthesizer synthesizer) {
        this.synthesizer = synthesizer;
        return this;
    }

    @Override
    public Chunk build() {
        // TODO - Validate fields

        // TODO - Get & validate audio file path
        File audioFile = new File(String.format("/chunks/chunk%d.mp3", id)); // TODO - Set /chunks/ as constant elsewhere

        // Create audio file using Synthesizer's Process
        synthesizer.save(text, audioFile);

        // If all good:
        return new Chunk(text, audioFile);
    }
}
