package main;

import controllers.Controller;
import events.StatusEvent;
import javafx.concurrent.Task;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSearcher {

    private List<String> urls = null;
    private Controller listener;

    public ImageSearcher(Controller c){
        listener = c;
    }

    public List<String> Search(String search, int num){

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key="+Keys.FLICKR_PUBLIC+
                "&text="+search+
                "&per_page="+num +
                "&sort=relevance"+
                "&extras=url_m";
        Request request = new Request.Builder().url(url).build();

        ExecutorService thread = Executors.newSingleThreadExecutor();
        Task<List<String>> call = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                try{
                    Response response = client.newCall(request).execute();
                    String XMLString = response.body().string();
                    System.out.println(XMLString);
                    XMLParser parser = new XMLParser();
                    return parser.parse(XMLString);
                }catch(IOException i){
                    i.printStackTrace();
                }
                return null;
            }
        };
        thread.submit(call);
        call.setOnSucceeded(event -> {
            urls = call.getValue();
            ImageDownload imageDownload = new ImageDownload(urls);
            ExecutorService imageThread = Executors.newSingleThreadExecutor();
            imageThread.submit(imageDownload);
            imageDownload.setOnSucceeded(event1 -> listener.handle(new StatusEvent(this,true)));

        });
        return null;
    }



}
