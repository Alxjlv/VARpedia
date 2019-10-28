package models.chunk;

import models.CallbackFileBuilder;
import models.FileManager;
import models.voice_synthesizer.VoiceSynthesizer;

import java.io.File;

/**
 * Implements a {@link CallbackFileBuilder} for {@link Chunk} objects
 * @author Tait & Alex
 */
public class ChunkFileBuilder implements CallbackFileBuilder<Chunk> {
    /**
     * The folder this chunk should be created in
     */
    private File chunkFolder;

    /**
     * The text this chunk should contain
     */
    private String text;

    /**
     * The synthesizer used to create the chunks audio
     */
    private VoiceSynthesizer voiceSynthesizer;

    /**
     * Package-private default constructor used by {@link ChunkFileManager}
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

    /**
     * Get the text of this chunk
     * @return The text of this chunk
     */
    public String getText() {
        return text;
    }

    /**
     * Set the {@link VoiceSynthesizer} for the chunk to be built
     * @param voiceSynthesizer The Synthesizer for the chunk
     * @return {@code this}
     */
    public ChunkFileBuilder setVoiceSynthesizer(VoiceSynthesizer voiceSynthesizer) {
        this.voiceSynthesizer = voiceSynthesizer;
        return this;
    }

    /* Build the chunk */
    @Override
    public void build(FileManager<Chunk> caller) {
        // Create audio file using Synthesizer's Process
        File audioFile = voiceSynthesizer.save(text, chunkFolder);

        // If all good:
        caller.save(new Chunk(text, voiceSynthesizer), audioFile);
    }
}
