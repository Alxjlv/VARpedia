package models;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * WikipediaSearcher makes calls to the Wikipedia API
 * @author Tait & Alex
 */
public class WikipediaSearcher {
    /**
     * Get a list of page titles that match the given search term
     * @param searchTerm The search term to search for matching pages
     * @return List of page titles that match the given search term
     * @throws IOException
     */
    public static List<String> GetPages(String searchTerm) throws IOException {
        JSONArray jsonArray = GetPagesJSON(searchTerm);

        List<String> pages = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            pages.add(jsonArray.getJSONObject(i).getString("title"));
        }

        return pages;
    }

    /**
     * Get the page introduction for the first matched page for the given search term
     * @param searchTerm The search term to search for the first matched page
     * @return The first matched page for the given search term
     * @throws IOException
     */
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

    /**
     * Helper method requests a JSON response from Wikipedia for pages that match the given search term
     * @param searchTerm Search term to match pages
     * @return JSONArray of pages
     * @throws IOException
     */
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
