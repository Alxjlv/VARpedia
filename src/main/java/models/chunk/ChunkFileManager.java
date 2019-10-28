package models.chunk;

import constants.Folder;
import models.FileManager;

import java.io.File;

/**
 * CreationFileManager is a singleton {@link FileManager} for {@link Creation} items. It ensures that {@link Creation}'s
 * exist with a video and thumbnail file, as well as creating and deleting new {@link Creation} objects.
 * @author Tait & Alex
 */

/**
 * ChunkFileManager is a singleton {@link FileManager} for {@link Chunk} items. It ensures that the items it contains
 * exist with temporary audio files.
 * @author Tait & Alex
 */
public class ChunkFileManager extends FileManager<Chunk> {
    /**
     * The singleton instance
     */
    private static ChunkFileManager instance;

    /**
     * The id to be given to the next Chunk
     */
    private int nextId;

    /**
     * Private constructor for singleton
     */
    private ChunkFileManager() {
        super();

        File chunksFolder = Folder.TEMP_CHUNKS.get();
        if (chunksFolder.exists()) {
            recursiveDelete(chunksFolder); // TODO Clear .chunks/ folder
        }
        chunksFolder.mkdirs();

        nextId = 0;
    }

    /**
     * Get the singleton instance
     * @return The singleton instance
     */
    public static ChunkFileManager getInstance() {
        if (instance == null) {
            synchronized (ChunkFileManager.class) {
                if (instance == null) {
                    instance = new ChunkFileManager();
                }
            }
        }
        return instance;
    }

    /**
     * Reset ChunkFileManager
     */
    public void reset() {
        nextId = 0;
        files.clear();
        items.clear();

        File chunksFolder = Folder.TEMP_CHUNKS.get();
        recursiveDelete(chunksFolder);
        chunksFolder.mkdirs();
    }

    /* Returns a ChunkFileBuilder */
    @Override
    public ChunkFileBuilder getBuilder() {
        File chunkFolder = new File(Folder.TEMP_CHUNKS.get(), Integer.toString(nextId++));
        chunkFolder.mkdirs();
        return new ChunkFileBuilder().setChunkFolder(chunkFolder);
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
