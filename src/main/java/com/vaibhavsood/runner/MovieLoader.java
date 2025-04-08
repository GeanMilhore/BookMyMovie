package com.vaibhavsood.runner;

import com.vaibhavsood.data.repository.MovieRepository;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class MovieLoader implements TableLoader {

    private final MovieRepository movieRepository;

    public MovieLoader(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void populate() {
        if (movieRepository.count() > 0L) return;

        CSVFile moviesFile = new CSVFile("movies.medium.csv");
        CSVFile linksFile = new CSVFile("links.csv");
        Iterator<String> allMovies = moviesFile.getFileLines();
        Iterator<String> allLinks = linksFile.getFileLines();
        MovieFileIterator movieFileIterator = new MovieFileIterator(allMovies, allLinks);
        processEachMovieFile(movieFileIterator);
    }

    private void processEachMovieFile(MovieFileIterator movieFileIterator) {
        while (movieFileIterator.hasNext())
            new MovieProcessor(movieFileIterator.next(), movieRepository).run();
    }
}
