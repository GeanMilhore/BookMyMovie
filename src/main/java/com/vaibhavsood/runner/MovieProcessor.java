package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieProcessor implements Runnable {

    private MovieFile movieFile;
    private MovieRepository movieRepository;
    private final Logger LOGGER;

    MovieProcessor(MovieFile movieFile, MovieRepository movieRepository) {
        LOGGER = LoggerFactory.getLogger(this.getClass());
        this.movieFile = movieFile;
        this.movieRepository = movieRepository;
    }

    @Override
    public void run() {
        LOGGER.info("{}:{}", Thread.currentThread().getId(), movieFile.getLinkLine());
        Movie movie = movieFile.make();
        String movieWebPath = movieFile.getWebPath();
        MoviePage moviePage = PageReader.requestPageDocument(movieWebPath);
        String posterUrl = moviePage.getPosterUrl();
        movie.setMoviePosterUrl(posterUrl);
        movieRepository.save(movie);
    }
}
