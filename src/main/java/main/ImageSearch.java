package main;

import javafx.concurrent.Task;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ImageSearch extends Task<Void> {
    private List<String> urlList;

    public ImageSearch(List<String> urls){
        urlList=urls;
    }

    @Override
    protected Void call() throws Exception {
        for(int i=0;i<urlList.size();i++){
            try(InputStream in = new URL(urlList.get(i)).openStream()){
                Files.copy(in, Paths.get("./"+i+".jpg"));//currently downloading images and placing them in root folder
                System.out.println("attempted downloading");
            }
        }


        return null;
    }
}
