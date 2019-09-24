package main;

import javafx.concurrent.Task;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Search {

    public List<String> Search(String search, int num){
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key="+Keys.FLICKR_PUBLIC+
                "&text="+search+
                "&per_page="+num +
                "&sort=relevance";
        Request request = new Request.Builder().url(url).build();

        ExecutorService thread = Executors.newSingleThreadExecutor();
        Task<Response> call = new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                try{
                    Response response = client.newCall(request).execute();
                    return response;
                }catch(IOException i){
                    i.printStackTrace();
                }
                return null;
            }
        };
        thread.submit(call);
        call.setOnSucceeded(event -> {
            Response r = call.getValue();
            try {
                String XMLString = r.body().string();
                XMLParser parser = new XMLParser();
                parser.parse(XMLString);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return null;
    }



}
