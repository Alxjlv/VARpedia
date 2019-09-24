package main;

import javafx.concurrent.Task;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ImageSearch extends Task<Void> {
    private List<String> urls;

    public ImageSearch(List<String> urls){
        urls=urls;
    }

    @Override
    protected Void call() throws Exception {
        for(String s:urls){
            try(InputStream in = new URL(s).openStream()){
                Files.copy(in, Paths.get("./resources"));
            }
        }


        return null;
    }
}
