package models.chunk;

import models.CallbackFileBuilder;
import models.FileManager;
import models.voice_synthesizer.VoiceSynthesizer;

import java.io.File;

/**
 * Implements a {@link CallbackFileBuilder} for {@link Chunk} objects
 */
public class ChunkFileBuilder implements CallbackFileBuilder<Chunk> {
    private File chunkFolder;
    private String text;
    private VoiceSynthesizer voiceSynthesizer;

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
     * Set the {@link VoiceSynthesizer} for the chunk to be built
     * @param voiceSynthesizer The Synthesizer for the chunk
     * @return {@code this}
     */
    public ChunkFileBuilder setVoiceSynthesizer(VoiceSynthesizer voiceSynthesizer) {
        this.voiceSynthesizer = voiceSynthesizer;
        return this;
    }

    @Override
    public void build(FileManager<Chunk> caller) {
        // TODO - Validate fields

        // TODO - Get & validate audio file path

        // Create audio file using Synthesizer's Process
        File audioFile = voiceSynthesizer.save(text, chunkFolder);

        // If all good:
        caller.save(new Chunk(text, voiceSynthesizer), audioFile);
    }
}
