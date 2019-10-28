package models.voice_synthesizer;

import main.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.Executors;

/**
 * EspeakVoiceSynthesizer implements the VoiceSynthesizer interface for Espeak
 * @author Tait & Alex
 */
public final class EspeakVoiceSynthesizer implements VoiceSynthesizer {
    /* Required for Serialization */
    private static final long serialVersionUID = 2538014884103997513L;

    /**
     * Voices supported by EspeakVoiceSynthesizer
     */
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

    /**
     * The selected voice for this instance
     */
    private Voice voice;

    /**
     * Public default constructor required for Externalizable. Field voice is set by readExternal().
     * Note: Users should not use this constructor.
     */
    public EspeakVoiceSynthesizer() {}

    /**
     * Constructs an EspeakVoiceSynthesizer that uses the given voice
     * Note: Users should use this constructor
     * @param voice The voice that this EspeakVoiceSynthesizer should use
     */
    public EspeakVoiceSynthesizer(Voice voice) {
        this.voice = voice;
    }

    /**
     * Get the voice used by this EspeakVoiceSynthesizer
     * @return
     */
    public Voice getVoice() {
        return voice;
    }

    /* Preview synthesized text audibly */
    @Override
    public ProcessRunner preview(String text) {
        ProcessRunner process = new ProcessRunner(String.format("espeak -v %s '%s'", voice.getCommand(), text));
        Executors.newSingleThreadExecutor().submit(process);
        return process;
    }

    /* Save synthesized text to a File */
    @Override
    public File save(String text, File folder) {
        File audioFile = new File(folder, "audio.wav");

        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-w", audioFile.getPath(), "-a", "200",
                "-v", voice.getCommand(), text);
        try {
            processBuilder.start();
        } catch (IOException e) {
            return null;
        }
        return audioFile;
    }

    /* Represent this EspeakVoiceSynthesizer by is Voice name */
    @Override
    public String toString() {
        return voice.getName();
    }

    /* Implement writing for Serialization */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(voice);
    }

    /* Implement reading for Serialization */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        voice = (Voice) in.readObject();
    }
}
