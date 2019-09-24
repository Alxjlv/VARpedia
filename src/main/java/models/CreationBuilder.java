package models;

import java.io.File;
import java.util.List;

public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
    private File videoFile;
    private List<Chunk> chunks;

    public CreationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CreationBuilder setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

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
