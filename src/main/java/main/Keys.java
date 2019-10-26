package main;

import java.io.*;

public class Keys {
    private static String FLICKR_PUBLIC;

    public static String getFlickrPublic(){
        FLICKR_PUBLIC = readKey();
        return FLICKR_PUBLIC;
    }

    private static String readKey() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(".bin/keys.txt"))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String flickrPublic = "FLICKR_PUBLIC";
                if(line.contains(flickrPublic)){
                    line = line.replaceAll("\\s+","");
                    line = line.replaceAll("=","");
                    line = line.replaceAll(flickrPublic,"");
                    return line;
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}