package models;

import java.io.*;
import java.util.List;

/**
 * Represents a users Creation
 */
public class Creation implements Externalizable {
    private static final long serialVersionUID = 361870838792448692L;

    private String name;
    private File videoFile;
    private List<Chunk> chunks;
//    private List<File> images;
    // TODO - Add Media
    // TODO - Add chunks
    // TODO - Add images
    // TODO - Add creation time?

    public Creation() {
        this(null, null, null);
    }

    public Creation(String name, File videoFile, List<Chunk> chunks) {
        this.name = name;
        this.videoFile = videoFile;
        this.chunks = chunks;
    }

    /**
     * Gets the name of the creation
     * @return The name of the creation
     */
    public String getName() {
        return name;
    }

    public File getVideoFile() {
        return videoFile;
    }

    /**
     * Gets the last modified time of the creation
     * @return The last modified time as seconds since Epoch
     */
    public long getLastModified() {
        return videoFile.lastModified();
    }

    /**
     * Gets the duration of the creation
     * @return The duration of the creation
     */
    public long getDuration() {
        return 0;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeObject(videoFile);
        out.writeObject(chunks);
//        out.writeObject(images);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        videoFile = (File) in.readObject();
        chunks = (List<Chunk>) in.readObject();
//        images = (List<File>) in.readObject();
    }
}
