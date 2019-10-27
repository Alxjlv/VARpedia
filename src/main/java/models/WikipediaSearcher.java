package models;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WikipediaSearcher {

    public static List<String> GetPages(String searchTerm) throws IOException {
        JSONArray jsonArray = GetPagesJSON(searchTerm);

        List<String> pages = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            pages.add(jsonArray.getJSONObject(i).getString("title"));
        }

        return pages;
    }

    public static String GetPage(String searchTerm) throws IOException {
        JSONArray jsonArray = GetPagesJSON(searchTerm);
        if (jsonArray.isEmpty()) {
            return null;
        }
        int pageId = jsonArray.getJSONObject(0).getInt("pageid");

        OkHttpClient client = new OkHttpClient();
        String url = "https://en.wikipedia.org/w/api.php?" +
                "action=query" +
                "&pageids=" + pageId +
                "&format=json" +
                "&prop=extracts" +
                "&exintro" +
                "&explaintext";
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();
        JSONObject jsonObject = new JSONObject(result);

        return jsonObject
                .getJSONObject("query")
                .getJSONObject("pages")
                .getJSONObject(Integer.toString(pageId))
                .getString("extract");
    }

    private static JSONArray GetPagesJSON(String searchTerm) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "https://en.wikipedia.org/w/api.php?action=query&list=prefixsearch&format=json" +
                "&pssearch="+searchTerm.toLowerCase();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();

        String result = response.body().string();
        JSONObject jsonObject = new JSONObject(result);
        return jsonObject.getJSONObject("query").getJSONArray("prefixsearch");
    }
}
