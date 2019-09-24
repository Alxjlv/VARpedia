package models;

import java.io.File;
import java.io.IOException;

public class Synthesizer {

    // TODO - Add Syntheiszer options - enums?

    public void preview(String text) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO - Error checking
        }
        // Process - play audibly
        // Throws exceptions?
    }

    public void save(String text, File audioFile) {
        ProcessBuilder processBuilder = new ProcessBuilder("espeak", "-w", audioFile.getPath() ,text);
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            // TODO Error checking
        }
        // Process - save text to audioFile
        // Throws exceptions?
    }
}
