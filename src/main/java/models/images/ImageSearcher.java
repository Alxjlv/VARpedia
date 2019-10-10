package models.images;

import javafx.concurrent.Task;
import main.Keys;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSearcher {

    private HashMap<URL, File> urls = null;

    public ImageSearcher(){

    }

    public void Search(String search, int num){
        System.out.println("Starting searching");
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key="+ Keys.FLICKR_PUBLIC+
                "&text="+search+
                "&per_page="+num +
                "&sort=relevance"+
                "&extras=url_m";
        Request request = new Request.Builder().url(url).build();

        ExecutorService thread = Executors.newSingleThreadExecutor();
        Task<HashMap<URL, File>> call = new Task<>() {
            @Override
            protected HashMap<URL, File> call() throws Exception {
                System.out.println("Started parsing xml");
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
            System.out.println("urls retrieved");
            urls = call.getValue();
            ImageManager.getInstance().requestComplete(urls);
        });
    }




}
