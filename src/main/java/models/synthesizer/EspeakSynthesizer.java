package models.synthesizer;

import main.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.Executors;

/**
 * Work in progress. See above comments for design ideas
 */
public final class EspeakSynthesizer extends Synthesizer {
    private static final long serialVersionUID = 2538014884103997513L;

    public enum Voice {
        BRITISH("British Male", "english+m1"),
        BRITISH_F("British Female", "english+f1"),
        SCOTTISH("Scottish Male", "en-scottish"),
        SCOTTISH_F("Scottish Female", "en-scottish+f2"),
        AMERICAN("American Male", "english-us+m3"),
        AMERICAN_F("American Female", "english-us+f3");


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
    public ProcessRunner preview(String text) {
        ProcessRunner process = new ProcessRunner(String.format("espeak -v %s \"%s\"", voice.getCommand(), text));
        Executors.newSingleThreadExecutor().submit(process);
        return process;
    }

    @Override
    public File save(String text, File folder) {
        File audioFile = new File(folder, "audio.wav");

        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-w", audioFile.getPath(),"-a","200",
                "-v", voice.getCommand(), text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Error checking
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
