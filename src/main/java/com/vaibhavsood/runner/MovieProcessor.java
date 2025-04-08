package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieProcessor implements Runnable {

    private final Logger LOGGER;
    private MovieRepository movieRepository;
    private Movie sourceMovie;
    private String movieWebPath;
    private String rawMovieLink;

    MovieProcessor(MovieFile movieFile, MovieRepository movieRepository) {
        LOGGER = LoggerFactory.getLogger(this.getClass());
        this.movieRepository = movieRepository;
        sourceMovie = movieFile.make();
        movieWebPath = movieFile.getWebPath();
        rawMovieLink = movieFile.getLinkLine();
    }

    @Override
    public void run() {
        LOGGER.info("{}:{}", Thread.currentThread().getId(), rawMovieLink);
        MoviePage moviePage = PageReader.requestPageDocument(movieWebPath);
        String posterUrl = moviePage.getPosterUrl();
        sourceMovie.setMoviePosterUrl(posterUrl);
        movieRepository.save(sourceMovie);
    }
}
