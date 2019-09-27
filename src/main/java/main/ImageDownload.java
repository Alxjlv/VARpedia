package main;

import javafx.concurrent.Task;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ImageDownload extends Task<Void> {
    private List<String> urlList;

    public ImageDownload(List<String> urls){
        urlList=urls;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("Starting download");
        for(int i=0;i<urlList.size();i++){
            int num = i+1;
            System.out.println("attempted downloading image " + num);
            try(InputStream in = new URL(urlList.get(i)).openStream()){
                System.out.println("got here");
                Files.copy(in, Paths.get("./.temp/"+num+".jpg"));//currently downloading images and placing them in root folder
                System.out.println("downloaded image number"+num);
            }
        }


        return null;
    }
}
