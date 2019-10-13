package main;

import java.io.*;

public class Keys {
    private static String FLICKR_PUBLIC;
    private static String flickrPublic = "FLICKR_PUBLIC";

    public static String getFlickrPublic(){
        FLICKR_PUBLIC = readKey();
        return FLICKR_PUBLIC;
    }

    private static String readKey() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("keys.txt"))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                System.out.println(line);
                if(line.contains(flickrPublic)){
                    line = line.replaceAll("\\s+","");
                    System.out.println(line);
                    line = line.replaceAll("=","");
                    System.out.println(line);
                    line = line.replaceAll(flickrPublic,"");
                    System.out.println(line);
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