package views;

import models.chunk.Chunk;

public class ChunkDragboard extends ObjectDragboard<Chunk> {
    private static final ChunkDragboard instance = new ChunkDragboard();

    public static ChunkDragboard getInstance() {
        return instance;
    }
}
