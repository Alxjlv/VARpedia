package models;

import javafx.collections.FXCollections;

import java.io.File;

public class ChunkManager extends Manager<Chunk> {
    private static ChunkManager instance;

    private static int id;

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
        items = FXCollections.observableArrayList();

        id = 0;

        // TODO Remove test data
        File file = new File("Final.mp4");
        items.add(new Chunk("Abc123", file));
        items.add(new Chunk("This is a chunk", file));
        items.add(new Chunk("This is another chunk", file));
        items.add(new Chunk("I prefer chocolate chunks", file));
        items.add(new Chunk("XYZ789", file));
    }

    @Override
    public ChunkBuilder getBuilder() {
        return new ChunkBuilder().setId(id);
    }

    public void reorder(Chunk source, Chunk target) {
        items.add(items.indexOf(target), items.remove(items.indexOf(source)));
    }
}
