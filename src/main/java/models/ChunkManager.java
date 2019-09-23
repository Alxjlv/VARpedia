package models;

import javafx.collections.FXCollections;

import java.io.File;

public class ChunkManager extends Manager<Chunk> {
    private static ChunkManager instance;

    private ChunkManager() {
    }

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

    // Clear folder
    // Load files
    // Instantiate items
    // Reset ChunkBuilder id counter
    @Override
    public void load() {
        File file = new File("Final.mp4");
        items = FXCollections.<Chunk>observableArrayList();
        items.add(new Chunk("Abc123", file));
        items.add(new Chunk("This is a chunk", file));
        items.add(new Chunk("This is another chunk", file));
        items.add(new Chunk("I prefer chocolate chunks", file));
        items.add(new Chunk("XYZ789", file));
    }
}
