package models.images;

import javafx.concurrent.Task;
import main.Keys;
import models.FormManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class responsible for making the Http request to Flickr
 */
public class ImageSearcher {

    private Map<URL, File> urls = new HashMap<>();

    public ImageSearcher(){

    }

    /**
     * This method makes the http request and parses the XML, and generates a HashMap
     * @param search - the term to search for
     * @param num - the number of images to search for
     */
    public void Search(String search, int num){
        System.out.println("Starting searching");
        //OkHttp is used to quite simply make Http requests
        OkHttpClient client = new OkHttpClient();
        //Constructing the Flickr API call
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key="+ Keys.FLICKR_PUBLIC+
                "&text="+search+
                "&per_page="+num +
                "&sort=relevance"+
                "&extras=url_m";
        Request request = new Request.Builder().url(url).build();

        ExecutorService thread = Executors.newSingleThreadExecutor();
        //Creating an extra thread to parse the XML
        Task<Map<URL, File>> call = new Task<Map<URL,File>>() {
            @Override
            protected Map<URL, File> call() throws Exception {
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
            //Sending the processed request to be downloaded
            FormManager.getInstance().getCurrentDownloader().requestComplete(urls);
        });
    }




}
