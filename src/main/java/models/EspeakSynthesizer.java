package models;

import java.io.File;
import java.io.IOException;

/**
 * Work in progress. See above comments for design ideas
 */
public final class EspeakSynthesizer extends Synthesizer {

    // TODO - Add voice options for Espeak

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

    public EspeakSynthesizer() {
        this.voice = Voice.DEFAULT;
    }

    public EspeakSynthesizer(Voice voice) {
        this.voice = voice;
    }

    @Override
    public void preview(String text) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", text);
        try {
            Process process = processBuilder.start();
            // TODO - Return status when done
        } catch (IOException e) {
            // TODO - Error checking
        }
    }

    @Override
    public void save(String text, File folder) {
        File audioPath = new File(folder, "audio.wav");

        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-w", audioPath.getPath(), text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Error checking
        }
    }
}
