package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.entity.Screen;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreenRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final int SCREEN_DEFAULT_SIZE = 14;
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
        if (!movieRepository.findAll().isEmpty()) return;

        CSVFile moviesFile = new CSVFile("movies.medium.csv");
        CSVFile linksFile = new CSVFile("links.csv");
        Iterator<String> allMovies = moviesFile.getFileLines();
        Iterator<String> allLinks = linksFile.getFileLines();
        MovieFileIterator movieFileIterator = new MovieFileIterator(allMovies, allLinks);
        processEachMovieFile(movieFileIterator);
    }

    private void processEachMovieFile(MovieFileIterator movieFileIterator) {
        while (movieFileIterator.hasNext())
            new ProcessMovie(movieFileIterator.next()).run();
    }

    private void populateScreeningsTable() {
        if (screenRepository.count() > SCREEN_DEFAULT_SIZE) return;
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

    private void saveShuffledScreeningMultipleTimes(Screening screening) {
        screeningRepository.save(screening);
        ScreeningShuffler shuffler = new ScreeningShuffler(screening);
        saveScreeningClone(shuffler);
        saveShuffledScreeningClones(shuffler);
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

    private Movie randomMovie() {
        return movieRepository.findRandom();
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public ScreeningRepository getScreeningRepository() {
        return screeningRepository;
    }

    class ProcessMovie implements Runnable {

        private MovieFile movieFile;

        ProcessMovie(MovieFile movieFile) {
            this.movieFile = movieFile;
        }

        @Override
        public void run() {
            LOGGER.info(Thread.currentThread().getId() + ":" + movieFile.getLinkLine());

            Movie movie = movieFile.make();
            String linkContextPath = movieFile.getContextPath();
            Document movieLensPage = null;
            try {
                movieLensPage = Jsoup.connect("https://www.imdb.com/title/tt" + linkContextPath).get();
            } catch (HttpStatusException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (movieLensPage != null) {
                Element image = movieLensPage.getElementsByClass("ipc-lockup-overlay ipc-focusable").first();
                movie.setMoviePosterUrl("imdb.com" + image.attr("href"));
            }

            movieRepository.save(movie);
        }
    }
}