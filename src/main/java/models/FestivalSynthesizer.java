package models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class FestivalSynthesizer extends Synthesizer {

    private final static File previewFile = new File("temp/preview.scm");

    public enum Voice {
        DEFAULT("default_voice"), // TODO - Set correct string
        NZ("nz_voice"); // TODO - Set correct string

        private final String voice;
        Voice(String voice) {
            this.voice = voice;
        }

        public String getVoice() {
            return voice;
        }
    }

    private final Voice voice;

    public FestivalSynthesizer() {
        this.voice = Voice.DEFAULT;
    }

    public FestivalSynthesizer(Voice voice) {
        this.voice = voice;
    }

    @Override
    public void preview(String text) {
        try {
            FileWriter writer = new FileWriter(previewFile);
            writer.write(String.format("(%s)", voice.getVoice()));
            writer.write(String.format("(SayText \"%s\")", text));
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

        File synthFile = new File(folder, "synth.txt");
        try {
            FileWriter writer = new FileWriter(textFile);
            writer.write(String.format("(%s)", voice.getVoice()));
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        File audioFile = new File(folder, "synth.txt");
        ProcessBuilder processBuilder = new ProcessBuilder("text2wave", "-o", audioFile.toString(),
                textFile.toString(), "-eval", synthFile.toString());
        try {
            Process process = processBuilder.start();
            // TODO - Return status when done
        } catch (IOException e) {
            // TODO - Error checking
        }
    }
}
