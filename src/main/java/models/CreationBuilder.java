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

    @Override
    public Creation build() {
        // TODO - Validate fields

        // TODO - Validate creation path

        // TODO - Create folder?
        // TODO - Create video using searchTerm and chunks

        return new Creation(name, videoFile);
    }
}
