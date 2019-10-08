package models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class FestivalSynthesizer extends Synthesizer {

    private final static File previewFile = new File(".temp/preview.scm");

    public enum Voice {
        KAL("kal_diphone"), // TODO - Set correct string
        NZ("akl_nz_jdt_diphone"); // TODO - Set correct string

        private final String name;
        Voice(String voice) {
            this.name = voice;
        }

        public String getName() {
            return name;
        }
    }

    private final Voice voice;

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
    public void preview(String text) {
        try {
            FileWriter writer = new FileWriter(previewFile);
            writer.write(String.format("(voice_%s)\n", voice.getName()));
            writer.write(String.format("(SayText \"%s\")\n", text));
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        ProcessBuilder processBuilder = new ProcessBuilder("festival", "-b", previewFile.toString());
        try {
            Process process = processBuilder.start();
            // TODO - Return status when done
        } catch (IOException e) {
            // TODO - Error checking
        }
    }

    @Override
    public void save(String text, File folder) {

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
            writer.write(String.format("(voice_%s)\n", voice.getName()));
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        File audioFile = new File(folder, "audio.wav");
        ProcessBuilder processBuilder = new ProcessBuilder("text2wave", "-o", audioFile.toString(),
                textFile.toString(), "-eval", synthFile.toString(), "-F", "22050");
        try {
            Process process = processBuilder.start();
            // TODO - Return status when done
        } catch (IOException e) {
            // TODO - Error checking
        }
    }
}
