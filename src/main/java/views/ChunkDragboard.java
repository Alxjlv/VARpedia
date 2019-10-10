package views;

import models.chunk.Chunk;

public class ChunkDragboard {
    private static final ChunkDragboard instance = new ChunkDragboard();

    private Chunk chunk;

    public static ChunkDragboard getInstance() {
        return instance;
    }

    public void set(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk get() {
        return chunk;
    }
}
