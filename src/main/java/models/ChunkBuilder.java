package models;

import java.io.File;

/**
 * Implements a {@link Builder} for {@link Chunk} objects
 */
public class ChunkBuilder implements Builder<Chunk> {
    private File chunkFolder;
    private String text;
    private Synthesizer synthesizer;

    /**
     * Package-private default constructor
     */
    ChunkBuilder() {}

    /**
     * Set the folder for the chunk to be built in. This method is package-private. Only intended to be called by
     * {@link ChunkManager}
     * @param chunkFolder The folder for the chunk to be built in
     * @return
     */
    ChunkBuilder setChunkFolder(File chunkFolder) {
        this.chunkFolder = chunkFolder;
        return this;
    }

    File getChunkFolder() {
        return chunkFolder;
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

    public String getText() {
        return text;
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

    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    @Override
    public Chunk build() {
        // TODO - Validate fields

        // TODO - Get & validate audio file path

        // Create audio file using Synthesizer's Process
        synthesizer.save(text, chunkFolder);

        // If all good:
        return new Chunk(text, synthesizer);
    }
}
