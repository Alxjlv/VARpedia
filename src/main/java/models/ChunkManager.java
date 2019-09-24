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

    @Override
    public void load() {
        // TODO Clear .chunks/ folder

        // Instantiate items
        items = FXCollections.<Chunk>observableArrayList();

        // TODO Reset ChunkBuilder id counter

        // TODO Remove test data
        File file = new File("Final.mp4");
        items.add(new Chunk("Abc123", file));
        items.add(new Chunk("This is a chunk", file));
        items.add(new Chunk("This is another chunk", file));
        items.add(new Chunk("I prefer chocolate chunks", file));
        items.add(new Chunk("XYZ789", file));
    }

    public void reorder(Chunk source, Chunk target) {
        int i = items.indexOf(source);
        int j = items.indexOf(target);

        items.add(j, items.remove(i));
    }
}
