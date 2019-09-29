package models;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import main.ProcessRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
    private File videoFile;
    private List<Chunk> chunks;
    private int numberOfImages;

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

    public CreationBuilder setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
        return this;
    }

//     TODO - Add setImages(List<Image> images)

//    private synchronized void setImageDuration(double duration){
//        imageDuration = duration;
//    }

    @Override
    public Creation build() {
        // TODO - Validate fields

        // TODO - Validate creation path/folder
        // Move temp folder to creation folder
        File tempFolder = new File("temp/");
        File creationsFolder = new File("creations/");

        File creationFolder = new File(creationsFolder, name);
        creationFolder.mkdir();

        List<String> combineAudioCommand = new ArrayList<>();
        combineAudioCommand.add("sox");
        for (Chunk chunk: chunks) {
            combineAudioCommand.add(new File(chunk.getFolder(), "audio.wav").toString());
        }
        combineAudioCommand.add("temp/combined.wav");
        System.out.println(combineAudioCommand);
        ProcessRunner combineAudio = new ProcessRunner(String.join(" ", combineAudioCommand));
        new Thread(combineAudio).start();
        combineAudio.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("exit value: "+combineAudio.getExitVal());
                Media combinedAudio = new Media(new File("temp/combined.wav").toURI().toString());
                MediaPlayer load = new MediaPlayer(combinedAudio);
                load.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        tempFolder.renameTo(creationFolder);

                        Duration creationDuration = combinedAudio.getDuration();
                        double imageDuration = creationDuration.divide(numberOfImages).toSeconds();
                        imageDuration +=1;

                        File slideshow = new File(creationFolder, "slideshow.txt");
                        try {
                            FileWriter writer = new FileWriter(slideshow);
                            String last = null;
                            for (int i = 1; i<=numberOfImages; i++) { //don't actually need File types for images, can just iterate through each image in the folder
                                last = String.format("file '"+creationFolder.toString()+"/images/%d.jpg'\n", i);
                                writer.write(last);
                                writer.write(String.format("duration %f\n", imageDuration));
                            }
                            if (last != null) {
                                writer.write(last);
                            }
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO - Remove?
                        }
                        File combined = new File(creationFolder,"combined.avi");
                        String combineCommand = "ffmpeg -f concat -i " + slideshow.toString() +" -vf scale=500:-2 -vsync vfr -pix_fmt yuv420p "+combined.toString() +" -v quiet";
                        System.out.println("Combine command: "+combineCommand);
                        ProcessRunner combiner = new ProcessRunner(combineCommand);
                        Executors.newSingleThreadExecutor().submit(combiner);
                        combiner.setOnSucceeded(event1 -> {
                            //TODO - progress sending
                            System.out.println("exit value of combiner: "+combiner.getExitVal());
                        });
                        videoFile = new File(creationFolder,name+".mp4");
                        String drawtext = "\"drawtext=fontfile=Montserrat-Regular:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=\'"+searchTerm+"\'\"";
                        String convertCommand = "ffmpeg -i "+combined.toString() +" -vf "+drawtext+ " -c:v libx264 -crf 19 -preset slow -c:a libfdk_aac -b:a 192k -ac 2 " + videoFile.toString() +" -v quiet";
                        System.out.println("Convert Command: "+ convertCommand);
                        ProcessRunner converter = new ProcessRunner(convertCommand);
                        Executors.newSingleThreadExecutor().submit(converter);
                        converter.setOnSucceeded(event1 -> {
                            //TODO - progress sending
                            System.out.println("exit value of converter: "+converter.getExitVal());
                        });
//                        List<String> command = new ArrayList<>();
//                        command.add("ffmpeg -f concat -i");
//                        command.add(slideshow.toString());
//                        command.add("-vf \"scale=500:-2,");
//                        command.add(String.format("drawtext=fontfile=Montserrat-Regular:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=h(h-text_h)/2:text'%s'", searchTerm));
//                        command.add("-vsync vfr -pix_fmt yuv420p slideshow.avi -v quiet");
//
////                        String command = String.format(
////                                "ffmpeg -f concat -i input.txt -vf \"scale=500:-2, drawtext=fontfile=Montserrat-Regular:" +
////                                        "fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=\'\"+searchTerm+\"\'\" -vsync vfr -pix_fmt yuv420p slideshow.avi -v quiet");
//                        ProcessRunner runner = new ProcessRunner(String.join(" ", command));
//                        new Thread(runner);
//                        runner.setOnSucceeded(event -> {
//                            //testing out using files
////                            File video = new File("./creations/"+name+"/slideshow.mp4");
//
//                        });
                    }
                });
            }
        });

//        Task clearChunks = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                ChunkManager.clear();
//                return null;
//            }
//        };

//        tempFolder.renameTo(creationFolder);

//        new Thread(clearChunks).start();

        // TODO - Calculate duration of images from combined audio
        //ffprobe combined audio
//        Task<Void> durationProbe = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                String command ="ffprobe -i ./creations/"+name+"/combined.wav -show_format -v quiet | sed -n 's/duration=//p'";
//                ProcessBuilder probeRunner = new ProcessBuilder("bash","-c",command);
//                Process durProbe = probeRunner.start();
//                durProbe.waitFor();
//                InputStream inputStream = durProbe.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(reader);
//                try {
//                    String line = bufferedReader.readLine();
//                    if(line != null){
//                        double convert = Double.parseDouble(line);
//                        System.out.println(convert);
//                        setImageDuration(convert/images);
//                        System.out.println(convert/images);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        };
//        ExecutorService thread = Executors.newSingleThreadExecutor();
//        thread.submit(durationProbe);
//        durationProbe.setOnSucceeded(event -> {
//
//
//
//        });

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

        return new Creation(name, videoFile); // TODO - Move
    }
}
