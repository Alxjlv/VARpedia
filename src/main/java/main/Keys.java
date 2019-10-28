package main;

import java.io.*;

/**
 * This class is responsible for reading the API key for flickr from the keys.txt file
 * @author Tait & Alex
 */
public class Keys {
    private static String FLICKR_PUBLIC;

    /**
     * This method simply returns the key to another class
     * @return - the formatted string for the API key
     */
    public static String getFlickrPublic(){
        FLICKR_PUBLIC = readKey();
        return FLICKR_PUBLIC;
    }

    /**
     * This method removes the whitespace and formats the key properly
     * @return - the formatted string for the API key
     */
    private static String readKey() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(".bin/keys.txt"))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String flickrPublic = "FLICKR_PUBLIC";
                if(line.contains(flickrPublic)){ // Checking the right line has been grabbed
                    line = line.replaceAll("\\s+",""); // Removing whitespace
                    line = line.replaceAll("=",""); // Removing the =
                    line = line.replaceAll(flickrPublic,""); // Removing the line start
                    return line;
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

}