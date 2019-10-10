package models;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Work in progress. See above comments for design ideas
 */
public final class EspeakSynthesizer extends Synthesizer {
    private static final long serialVersionUID = 2538014884103997513L;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(voice);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        voice = (Voice) in.readObject();
    }

    // TODO - Add voice options for Espeak

    public enum Voice {
        BRITISH("British", "english"),
        SCOTTISH("Scottish", "en-scottish"),
        AMERICAN("American", "english-us");

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

    public EspeakSynthesizer() {
        this.voice = Voice.BRITISH;
    }

    public EspeakSynthesizer(Voice voice) {
        this.voice = voice;
    }

    public Voice getVoice() {
        return voice;
    }

    @Override
    public void preview(String text) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-v", voice.getCommand(), text);
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
                "-v", voice.getCommand(), text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Error checking
        }
    }

    @Override
    public String toString() {
        return voice.getName();
    }
}
