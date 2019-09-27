package models;

import java.io.File;
import java.io.IOException;

// TODO - Rename to SpeechSynthesizer?
// TODO - Make Synthesizer abstract?
// TODO - Make Concrete EspeakSynthesizer and FestivalSynthesizer?
// TODO - Make SynthesizerBuilder? Used in synthesizer options popup window (use enums for dropdown?)
/**
 * Work in progress. See above comments for design ideas
 */
public class EspeakSynthesizer extends Synthesizer {

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
