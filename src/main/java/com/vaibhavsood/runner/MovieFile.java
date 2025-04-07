package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;

public class MovieFile {

    private String movieLine;
    private String linkLine;

    public MovieFile(String movieLine, String linkLine) {
        this.movieLine = movieLine;
        this.linkLine = linkLine;
    }

    public Movie make(){
        String[] movieInfo = movieLine.split(",");
        Long movieID = getParsedMovieID(movieInfo);
        String movieTags = getMovieTags(movieInfo);
        String movieName = getMovieName();

        Movie movie = new Movie();
        movie.setMovieId(movieID);
        movie.setMovieName(movieName);
        movie.setMovieTags(movieTags);
        return movie;
    }

    private long getParsedMovieID(String[] movieInfo) {
        String movieID = movieInfo[0];
        return Long.parseLong(movieID);
    }

    private String getMovieTags(String[] movieInfo) {
        return movieInfo[movieInfo.length - 1];
    }

    private String getMovieName() {
        int nameBegin = movieLine.indexOf(",") + 1;
        int nameEnd = movieLine.lastIndexOf(",");
        return movieLine.subSequence(nameBegin, nameEnd).toString().trim();
    }

    public String getWebPath() {
        return linkLine.split(",")[1];
    }

    public String getLinkLine() {
        return linkLine;
    }
}


