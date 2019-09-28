package models;

import javafx.concurrent.Task;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
    private File videoFile;
    private List<Chunk> chunks;
    private int images;
    private double imageDuration;

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

    public CreationBuilder setNumberOfImages(int number) {
        this.images = images;
        return this;
    }

    // TODO - Add setImages(List<Image> images)

    private synchronized void setImageDuration(double duration){
        imageDuration = duration;
    }

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
        //ffprobe combined audio
        Task<Void> durationProbe = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String command ="ffprobe -i ./creations/"+name+"/combined.wav -show_format -v quiet | sed -n 's/duration=//p'";
                ProcessBuilder probeRunner = new ProcessBuilder("bash","-c",command);
                Process durProbe = probeRunner.start();
                durProbe.waitFor();
                InputStream inputStream = durProbe.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                try {
                    String line = bufferedReader.readLine();
                    if(line != null){
                        double convert = Double.parseDouble(line);
                        System.out.println(convert);
                        setImageDuration(convert/images);
                        System.out.println(convert/images);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        ExecutorService thread = Executors.newSingleThreadExecutor();
        thread.submit(durationProbe);
        durationProbe.setOnSucceeded(event -> {
            File slideshow = new File(creationFolder, "slideshow.txt");
            try {
                FileWriter writer = new FileWriter(slideshow);
                String last = null;
                for (int i = 1;i<=images;i++) { //don't actually need File types for images, can just iterate through each image in the folder
                last = String.format("file '%s'", i+".jpg");
                writer.write(last);
                writer.write(String.format("duration %f", imageDuration));
                }
                if (last != null) {
                    writer.write(last);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        //This is creating the settings for the slideshow

        // TODO - Process runner: "ffmpeg -f concat -i slideshow.txt -vsync vfr -pix_fmt yuv420p output.mp4 -v quiet -y"

        // TODO - Process runner: Combine audio, video and search term overlay using FFmpeg
        // TODO - Create video from: searchTerm, images and chunks using FFmpeg
        //Video creation
        // TODO - Setting up the input.txt file
//        String command = "ffmpeg -f concat -i input.txt -vsync vfr -pix_fmt yuv420p slideshow.mp4 -v quiet -y";
//        ProcessRunner runner = new ProcessRunner(command);
//        ExecutorService thread = Executors.newSingleThreadExecutor();
//        thread.submit(runner);
//        runner.setOnSucceeded(event -> {
//            //testing out using files
//            File slideshow = new File("./creations/"+name+"/slideshow.mp4");
//
//        });

        return new Creation(name, videoFile);
    }
}
