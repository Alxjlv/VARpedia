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

    private Voice voice;

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
