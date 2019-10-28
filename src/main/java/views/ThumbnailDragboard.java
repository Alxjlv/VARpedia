package views;

import java.net.URL;

/**
 * ChunkDragboard implements a singleton {@link ObjectDragboard<URL>} to support drag-and-drop for Images.
 * @author Tait & Alex
 */
public class ThumbnailDragboard extends ObjectDragboard<URL> {
    /**
     * Singleton instance
     */
    private static final ThumbnailDragboard instance = new ThumbnailDragboard();

    /**
     * Get the singleton instance
     * @return The singleton ThumbnailDragboard
     */
    public static ThumbnailDragboard getInstance() {
        return instance;
    }
}
