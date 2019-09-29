package models;

import javafx.collections.FXCollections;

import java.io.File;

/**
 * Implments {@link Manager} for {@link Chunk} objects
 */
public class ChunkManager extends Manager<Chunk> {
    private static ChunkManager instance;

    private int id;

    private final File chunksFolder;

    private ChunkManager() {
        this.chunksFolder = new File(".temp/chunks/");
        if (this.chunksFolder.exists()) {
            recursiveDelete(this.chunksFolder); // TODO Clear .chunks/ folder
        }
        this.chunksFolder.mkdirs();

        items = FXCollections.observableArrayList();

        id = 0;
    }

    @Override
    public ChunkBuilder getBuilder() {
        return new ChunkBuilder().setChunksFolder(chunksFolder).setId(id++);
    }

    @Override
    public void delete(Chunk chunk) {
        if (recursiveDelete(chunk.getFolder())) {
            super.delete(chunk);
        }
    }

    /**
     * Get the singleton instance
     * @return The singleton instance
     */
    public static ChunkManager getInstance() {
        if (instance == null) {
            synchronized (ChunkManager.class) {
                if (instance == null) {
                    instance = new ChunkManager();
                }
            }
        }
        return instance;
    }

    public synchronized static void newInstance() {
        instance = new ChunkManager();
    }

    /**
     * Reorder items by placing source at the index of target. All items with index greater than or equal to target are
     * incremented.
     * @param source The {@link Chunk} to move
     * @param target The {@link Chunk} with the intended index of {@param source}
     */
    public void reorder(Chunk source, Chunk target) {
        items.add(items.indexOf(target), items.remove(items.indexOf(source)));
    }
}
