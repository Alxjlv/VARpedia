package models;

public class SearchManager {
    private static SearchManager instance;
    private String searchTerm;
    private Object lock = new Object();

    private SearchManager() {
    }
    public static SearchManager getInstance() {
        if (instance == null) {
            synchronized (SearchManager.class) {
                if (instance == null) {
                    instance = new SearchManager();
                }
            }
        }
        return instance;
    }

    public void setSearchTerm(String search){
        synchronized(lock){
            searchTerm=search;
        }
    }

    public String getSearchTerm(){
        return searchTerm;
    }
}
