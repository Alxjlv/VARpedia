package models.chunk;

import models.AsynchronousFileBuilder;
import models.FileManager;
import models.synthesizer.Synthesizer;

import java.io.File;

/**
 * Implements a {@link AsynchronousFileBuilder} for {@link Chunk} objects
 */
public class ChunkFileBuilder implements AsynchronousFileBuilder<Chunk> {
    private File chunkFolder;
    private String text;
    private Synthesizer synthesizer;

    /**
     * Package-private default constructor
     */
    ChunkFileBuilder() {}

    /**
     * Set the folder for the chunk to be built in. This method is package-private. Only intended to be called by
     * {@link ChunkFileManager}
     * @param chunkFolder The folder for the chunk to be built in
     * @return
     */
    ChunkFileBuilder setChunkFolder(File chunkFolder) {
        this.chunkFolder = chunkFolder;
        return this;
    }

    /**
     * Set the text to be spoken for the chunk to be built
     * @param text The text to be spoken
     * @return {@code this}
     */
    public ChunkFileBuilder setText(String text) {
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
    public ChunkFileBuilder setSynthesizer(Synthesizer synthesizer) {
        this.synthesizer = synthesizer;
        return this;
    }

    @Override
    public void build(FileManager<Chunk> caller) {
        // TODO - Validate fields

        // TODO - Get & validate audio file path

        // Create audio file using Synthesizer's Process
        File audioFile = synthesizer.save(text, chunkFolder);

        // If all good:
        caller.save(new Chunk(text, synthesizer), audioFile);
    }
}