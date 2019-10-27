package views;

import models.chunk.Chunk;

/**
 * ChunkDragboard implements a singleton {@link ObjectDragboard} to support drag-and-drop for {@link ChunkCell}.
 * @author Tait & Alex
 */
public class ChunkDragboard extends ObjectDragboard<Chunk> {
    /**
     * Singleton instance
     */
    private static final ChunkDragboard instance = new ChunkDragboard();

    /**
     * Get the singleton instance
     * @return The singleton ChunkDragboard
     */
    public static ChunkDragboard getInstance() {
        return instance;
    }
}
