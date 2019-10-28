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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class searches Flickr for images
 * @author Tait & Alex
 */
public class ImageSearcher {
    /**
     * Search Flickr for images that match the given search term
     * @param searchTerm - Search for images that match this term
     * @param num - The number of images to search for
     */
    public static void Search(String searchTerm, int num) {
        OkHttpClient client = new OkHttpClient();
        //Constructing the Flickr API call
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key="+ Keys.getFlickrPublic()+
                "&text="+searchTerm+
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
            List<URL> images = call.getValue();
            FormManager.getInstance().setImages(FXCollections.observableArrayList(images.subList(0,10)));

            ImageFileManager.getInstance().downloadImages(FormManager.getInstance().getImages());
        });
    }
}
