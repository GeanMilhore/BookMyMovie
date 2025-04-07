package com.vaibhavsood.runner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageReader {

    private static final String BASE_URL = "https://www.imdb.com/title/tt";

    public static MoviePage requestPageDocument(String path){
        String fullUrl = BASE_URL + path;
        Document document = requestFromUrl(fullUrl);
        if(document == null) throw new RuntimeException("Document not found!");
        return new MoviePage(document, BASE_URL);
    }

    private static Document requestFromUrl(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            System.out.println("failed to request document on website "+ url);
            throw new RuntimeException(e);
        }
    }
}
