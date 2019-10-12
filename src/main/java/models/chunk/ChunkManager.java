package models.chunk;

import constants.Filename;
import constants.Folder;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import main.ProcessRunner;
import models.Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implments {@link Manager} for {@link Chunk} objects
 */
public class ChunkManager extends Manager<Chunk> {
    private static ChunkManager instance;

    private int id;

    private final File chunksFolder;

    private Map<Chunk, File> chunkFiles;

    private ChunkManager() {
        chunksFolder = Folder.TEMP_CHUNKS.get();
        if (chunksFolder.exists()) {
            recursiveDelete(chunksFolder); // TODO Clear .chunks/ folder
        }
        chunksFolder.mkdirs();

        items = FXCollections.observableArrayList();

        id = 0;

        chunkFiles = new HashMap<>();
//        for (Chunk chunk: items) {
//            // TODO - Build chunks
//        }
    }

    public File getChunkFile(Chunk chunk) {
        return chunkFiles.get(chunk);
    }

    public void combine(EventHandler<WorkerStateEvent> handler) {
        File combinedAudio = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());
        String combineAudioCommand;
        if (items.size() == 1) {
            combineAudioCommand = String.format("mv '%s' '%s'", new File(ChunkManager.getInstance().getChunkFile(items.get(0)), Filename.CHUNK_AUDIO.get()).toString(), combinedAudio.getPath());
        } else {
            List<String> combineAudioCommandList = new ArrayList<>();
            combineAudioCommandList.add("sox");
            for (Chunk chunk : items) {
                combineAudioCommandList.add(String.format("'%s'",
                        new File(ChunkManager.getInstance().getChunkFile(chunk), Filename.CHUNK_AUDIO.get()).toString())
                );
            }
            combineAudioCommandList.add(combinedAudio.getPath());
            combineAudioCommand = String.join(" ", combineAudioCommandList);
        }

        ProcessRunner combineAudio = new ProcessRunner(combineAudioCommand);
        new Thread(combineAudio).start();
        combineAudio.setOnSucceeded(event -> {
            System.out.println("exit value of combineAudio: " + combineAudio.getExitValue());
            handler.handle(event);
        });
    }

    @Override
    public ChunkBuilder getBuilder() {
        File chunkFolder = new File(chunksFolder, Integer.toString(id++));
        chunkFolder.mkdirs();
        return new ChunkBuilder().setChunkFolder(chunkFolder);
    }

    public void create(ChunkBuilder builder) {
        Chunk chunk = builder.build();
        items.add(chunk);
        chunkFiles.put(chunk, builder.getChunkFolder());
    }

    @Override
    public void delete(Chunk chunk) {
        if (recursiveDelete(chunkFiles.get(chunk))) {
            chunkFiles.remove(chunk);
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
