package main;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Set;

public class Search {

//    private String _apiKey = Keys.FLICKR_PUBLIC;
//    private String _apiSecret = Keys.FLICKR_SECRET;
//
//    private Flickr flickr;
//    private REST rest;
//    private RequestContext requestContext;
//
//    public Search(){
//        flickr = new Flickr(_apiKey,_apiSecret, new REST());
//        requestContext = RequestContext.getRequestContext();
//        Flickr.debugRequest = false;
//        Flickr.debugStream = false;
//    }
//
//    public void search(String search){
//        PhotosInterface photos = flickr.getPhotosInterface();
//        SearchParameters params = new SearchParameters();
//        params.setSort(6);
//        params.setText(search);
//        try{
//            PhotoList<Photo> results = photos.search(params,5,0);
//            for(Photo p : results){
//                System.out.println(String.format("Title: %s",p.getTitle()));
//                System.out.println("Media: " + p.getMedia());
//                System.out.println("Original URL: " + p.getOriginalUrl());
//            }
//        }catch (FlickrException f){
//
//
//        }
//
//
//
//    }
    try{
        HttpClient httpClient = HttpClientBuilder.create().build();
        
    }

}
