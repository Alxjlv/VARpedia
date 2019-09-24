package models;

import java.io.File;
import java.util.List;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
    private File videoFile;
    private List<Chunk> chunks;

    /**
     * Set the name of the creation to be built
     * @param name The name of the creation to be built
     * @return {@code this}
     */
    public CreationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the search term of the creation to be built
     * @param searchTerm The search term of the creation to be built
     * @return {@code this}
     */
    public CreationBuilder setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    /**
     * Set the chunks of the creation to be built
     * @param chunks The chunks of the creation to be built
     * @return {@code this}
     */
    public CreationBuilder setChunks(List<Chunk> chunks) {
        this.chunks = chunks;
        return this;
    }

    // TODO - Add setImages(List<Image> images)

    @Override
    public Creation build() {
        // TODO - Validate fields

        // TODO - Validate creation path/folder

        // TODO - Create video from: searchTerm, images and chunks using FFmpeg

        return new Creation(name, videoFile);
    }
}
