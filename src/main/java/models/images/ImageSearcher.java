package models.images;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import main.Keys;
import models.FormManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class responsible for making the Http request to Flickr
 */
public class ImageSearcher {
    private List<URL> urls = new ArrayList<>();

    /**
     * This method makes the http request and parses the XML, and generates a HashMap
     * @param search - the term to search for
     * @param num - the number of images to search for
     */
    public void Search(String search, int num) {
        OkHttpClient client = new OkHttpClient();
        //Constructing the Flickr API call
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key="+ Keys.getFlickrPublic()+
                "&text="+search+
                "&per_page="+num +
                "&sort=relevance"+
                "&extras=url_m";
        Request request = new Request.Builder().url(url).build();

        ExecutorService thread = Executors.newSingleThreadExecutor();
        //Creating an extra thread to parse the XML
        Task<List<URL>> call = new Task<List<URL>>() {
            @Override
            protected List<URL> call() {
                try {
                    Response response = client.newCall(request).execute();
                    String XMLString = response.body().string();
                    XMLParser parser = new XMLParser();
                    return parser.parse(XMLString);
                } catch (IOException i) {
                    i.printStackTrace();
                }
                return null;
            }
        };
        thread.submit(call);
        call.setOnSucceeded(event -> {
            urls = call.getValue();
            FormManager.getInstance().setImages(FXCollections.observableArrayList(urls.subList(0,10)));

            ImageDownloader downloader = new ImageDownloader();
            downloader.downloadImages(FormManager.getInstance().getImages());
        });
    }




}
