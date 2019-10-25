package models.chunk;

import constants.Filename;
import constants.Folder;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import main.ProcessRunner;
import models.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implments {@link FileManager} for {@link Chunk} objects
 */
public class ChunkFileManager extends FileManager<Chunk> {
    private static ChunkFileManager instance;

    private int id;

    private final File chunksFolder;

    private ChunkFileManager() {
        super();

        chunksFolder = Folder.TEMP_CHUNKS.get();
        if (chunksFolder.exists()) {
            recursiveDelete(chunksFolder); // TODO Clear .chunks/ folder
        }
        chunksFolder.mkdirs();

        id = 0;
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

    public void reset() {
        id = 0;
        files.clear();
        items.clear();
        recursiveDelete(chunksFolder);
        chunksFolder.mkdirs();
    }

    @Override
    public ChunkFileBuilder getBuilder() {
        File chunkFolder = new File(chunksFolder, Integer.toString(id++));
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
