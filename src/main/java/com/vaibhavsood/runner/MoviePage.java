package com.vaibhavsood.runner;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MoviePage {

    private Document pageDocument;

    public MoviePage(Document pageDocument) {
        if(pageDocument == null) throw new RuntimeException("Document doesn't exist!") ;
        this.pageDocument = pageDocument;
    }

    public String getPosterUrl(){
        Element first = pageDocument.getElementsByClass("ipc-lockup-overlay ipc-focusable").first();
        return "imdb.com" + first.attr("href");
    }
}
