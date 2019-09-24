package models;

import java.io.File;
import java.io.IOException;

// TODO - Make Synthesizer abstract? Concrete EspeakSynethizer and FestivalSynthesizer?
// TODO - Make SynthesizerBuilder? Used in synth. options popup window (use enums for dropdown?)
/**
 * Work in progress. See above comments for design ideas
 */
public class Synthesizer {
    /**
     * Audibly plays the specified text as speech
     * @param text The text to be spoken
     */
    public void preview(String text) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO - Error checking
        }
    }

    /**
     * Saves the specified text as speech to the specified audio file
     * @param text The text to be spoken
     * @param audioFile The file to save
     */
    public void save(String text, File audioFile) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-w", audioFile.getPath() ,text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Error checking
        }
    }
}
