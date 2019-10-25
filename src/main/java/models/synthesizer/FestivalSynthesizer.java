package models.synthesizer;

import constants.Filename;
import constants.Folder;
import main.ProcessRunner;

import java.io.*;
import java.util.concurrent.Executors;

public final class FestivalSynthesizer extends Synthesizer {
    private static final long serialVersionUID = 1013614822749434394L;
    private static final File previewFile = new File(Folder.TEMP.get(), "/preview.scm");

    public enum Voice {
        NZ("Kiwi", "akl_nz_jdt_diphone"),
        KAL("Natural", "kal_diphone");

        private final String name;
        private final String command;
        Voice(String name, String command) {
            this.name = name;
            this.command = command;
        }

        public String getName() {
            return name;
        }

        public String getCommand() {
            return command;
        }
    }

    private Voice voice;

    public FestivalSynthesizer() {
        this.voice = Voice.KAL;
    }

    public FestivalSynthesizer(Voice voice) {
        this.voice = voice;
    }

    public Voice getVoice() {
        return voice;
    }

    @Override
    public ProcessRunner preview(String text) {
        try {
            FileWriter writer = new FileWriter(previewFile);
            writer.write(String.format("(voice_%s)\n", voice.getCommand()));
            writer.write(String.format("(SayText \"%s\")\n", text));
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        ProcessRunner processRunner = new ProcessRunner(String.format("festival -b %s", previewFile.toString()));
        Executors.newSingleThreadExecutor().submit(processRunner);
        return processRunner;
    }

    @Override
    public File save(String text, File folder) {
        File textFile = new File(folder, "text.txt");
        try {
            FileWriter writer = new FileWriter(textFile);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        File synthFile = new File(folder, "synth.scm");
        try {
            FileWriter writer = new FileWriter(synthFile);
            writer.write(String.format("(voice_%s)\n", voice.getCommand()));
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        File audioFile = new File(folder, Filename.CHUNK_AUDIO.get());
        ProcessBuilder processBuilder = new ProcessBuilder("text2wave", "-o", audioFile.toString(),
                textFile.toString(), "-eval", synthFile.toString(), "-F", "22050");
        try {
            Process process = processBuilder.start();
            // TODO - Return status when done
        } catch (IOException e) {
            // TODO - Error checking
        }
        return audioFile;
    }

    @Override
    public String toString() {
        return voice.getName();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(voice);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        voice = (Voice) in.readObject();
    }
}
