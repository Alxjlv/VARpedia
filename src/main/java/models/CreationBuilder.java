package models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
    private File videoFile;
    private List<Chunk> chunks;
    private List<File> images;

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

    public CreationBuilder setImages(List<File> images) {
        this.images = images;
        return this;
    }

    // TODO - Add setImages(List<Image> images)

    @Override
    public Creation build() {
        // TODO - Validate fields

        // TODO - Validate creation path/folder
        // Move temp folder to creation folder
        File tempFolder = new File("temp/");
        File creationsFolder = new File("creations/");

        File creationFolder = new File(creationsFolder, name);

        tempFolder.renameTo(creationFolder);


        // TODO - Calculate duration of images from combined audio
        double imageDuration = 5.0;


        File slideshow = new File(creationFolder, "slideshow.txt");
        try {
            FileWriter writer = new FileWriter(slideshow);
            String last = null;
            for (File image: images) {
                last = String.format("file '%s'", image.getPath());
                writer.write(last);
                writer.write(String.format("duration %f", imageDuration));
            }
            if (last != null) {
                writer.write(last);
            }
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        // TODO - Process runner: "ffmpeg -f concat -i slideshow.txt -vsync vfr -pix_fmt yuv420p output.mp4 -v quiet -y"

        // TODO - Process runner: Combine audio, video and search term overlay using FFmpeg

        return new Creation(name, videoFile);
    }
}
