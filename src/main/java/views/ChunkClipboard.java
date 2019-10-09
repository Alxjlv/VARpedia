package views;

import models.Chunk;

public class ChunkClipboard {
    private static final ChunkClipboard instance = new ChunkClipboard();

    private Chunk chunk;

    public static ChunkClipboard getInstance() {
        return instance;
    }

    public void set(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk get() {
        return chunk;
    }
}
