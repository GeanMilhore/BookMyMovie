package com.vaibhavsood.runner;

import java.util.Iterator;

public class MovieFileIterator {

    private Iterator<String> allMovies;
    private Iterator<String> allLinks;

    MovieFileIterator(Iterator<String> allMovies, Iterator<String> allLinks){
        this.allMovies = allMovies;
        this.allLinks = allLinks;
    }

    public Boolean hasNext(){
        return allMovies.hasNext() && allLinks.hasNext();
    }

    public MovieFile next(){
        String movie = allMovies.next();
        String link = allLinks.next();
        return new MovieFile(movie, link);
    }
}
