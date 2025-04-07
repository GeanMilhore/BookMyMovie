package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.entity.Screen;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreenRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private MovieRepository movieRepository;
    private ScreenRepository screenRepository;
    private ScreeningRepository screeningRepository;

    @Autowired
    public DataLoader(MovieRepository movieRepository, ScreeningRepository screeningRepository, ScreenRepository screenRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
        this.screenRepository = screenRepository;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        populateMovieTable();
        populateScreeningsTable();
    }

    private void populateMovieTable() {
        if (movieRepository.count() > 0l) return;

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

    private void populateScreeningsTable() {
        if (screeningRepository.count() > 0l) return;
        for (int theaterId = 1; theaterId <= 5; theaterId++)
            populateAllScreensOfTheater(theaterId);
    }

    private void populateAllScreensOfTheater(int theaterId) {
        List<Screen> theaterScreens = screenRepository.findByTheatreId(theaterId);
        populateScreeningsForEachScreen(theaterScreens);
    }

    private void populateScreeningsForEachScreen(List<Screen> screens) {
        for (Screen screen : screens)
            saveScreeningsBasedOn(screen);
    }

    private void saveScreeningsBasedOn(Screen screen) {
        Screening screening = ScreeningFactory.makeRandom(screen);
        screening.setMovieName(randomMovie().getMovieName());
        saveShuffledScreeningMultipleTimes(screening);
    }

    private Movie randomMovie() {
        return movieRepository.findRandom();
    }

    private void saveShuffledScreeningMultipleTimes(Screening screening) {
        ScreeningShuffler shuffler = new ScreeningShuffler(screening);
        saveScreeningClone(shuffler);
        saveShuffledScreeningClones(shuffler);
        screeningRepository.save(screening);
    }

    private void saveScreeningClone(ScreeningShuffler shuffler) {
        Screening screeningClone = shuffler.cloneAndShuffleTime();
        screeningRepository.save(screeningClone);
    }

    private void saveShuffledScreeningClones(ScreeningShuffler shuffler) {
        Screening shuffledScreening = shuffler.cloneAndIncreaseDaysBy(1);
        Screening shuffledScreeningClone = shuffler.cloneAndShuffleTime();
        screeningRepository.save(shuffledScreening);
        screeningRepository.save(shuffledScreeningClone);
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public ScreeningRepository getScreeningRepository() {
        return screeningRepository;
    }
}