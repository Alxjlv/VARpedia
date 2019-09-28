package models;

import java.io.File;
import java.io.IOException;

/**
 * Work in progress. See above comments for design ideas
 */
public final class EspeakSynthesizer extends Synthesizer {

    // TODO - Add voice options for Espeak

    public enum Voice {
        DEFAULT("default"),
        BRITISH("english"),
        SCOTTISH("en-scottish"),
        AMERICAN("english-us");

        private final String name;
        Voice(String voice) {
            this.name = voice;
        }

        public String getName() {
            return name;
        }
    }

    private final Voice voice;

    public EspeakSynthesizer() {
        this.voice = Voice.DEFAULT;
    }

    public EspeakSynthesizer(Voice voice) {
        this.voice = voice;
    }

    public Voice getVoice() {
        return voice;
    }

    @Override
    public void preview(String text) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-v", voice.getName(), text);
        System.out.println(voice.getName());
        System.out.println(processBuilder.command());

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

        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-w", audioPath.getPath(),
                "-v", voice.getName(), text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Error checking
        }
    }
}
