package models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FestivalSynthesizer extends Synthesizer {

    private static File previewFile = new File("temp/preview.scm");

    @Override
    public void preview(String text) {
        try {
            FileWriter writer = new FileWriter(previewFile);
            // TODO - Write voice
            // TODO - Write text
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
            // TODO - Write text
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        File synthFile = new File(folder, "synth.txt");
        try {
            FileWriter writer = new FileWriter(textFile);
            // TODO - Write synth settings
            writer.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }

        File audioFile = new File(folder, "synth.txt");
//        ProcessBuilder processBuilder = new ProcessBuilder("text2wave", "-o", audioFile.toString(),
//                textFile.toString(), "-eval", synthFile.toString());
//        try {
//            Process process = processBuilder.start();
//            // TODO - Return status when done
//        } catch (IOException e) {
//            // TODO - Error checking
//        }
    }
}
