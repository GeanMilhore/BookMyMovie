package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.entity.Screen;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreenRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import com.vaibhavsood.utils.DateUtils;
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
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private MovieRepository movieRepository;
    private ScreenRepository screenRepository;
    private ScreeningRepository screeningRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final int SCREEN_DEFAULT_SIZE = 14;

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public ScreeningRepository getScreeningRepository() {
        return screeningRepository;
    }

    @Autowired
    public DataLoader(MovieRepository movieRepository, ScreeningRepository screeningRepository,
                      ScreenRepository screenRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
        this.screenRepository = screenRepository;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        populateMovieTable();
        populateScreeningsTable();
    }

    private void populateMovieTable() {
        if (!movieRepository.findAll().isEmpty()) return;

        CSVFile moviesFile = new CSVFile("movies.medium.csv");
        CSVFile linksFile = new CSVFile("links.csv");
        Iterator<String> allMovies = moviesFile.getFileLines();
        Iterator<String> allLinks = linksFile.getFileLines();
        processMoviesAndLinks(allMovies, allLinks);
    }

    private void processMoviesAndLinks(Iterator<String> allMovies, Iterator<String> allLinks) {
        while (allMovies.hasNext())
            processNextMovie(allMovies.next(), allLinks.next());
    }

    private void processNextMovie(String movie, String link) {
        new ProcessMovie(movie, link).run();
    }

    private void populateScreeningsTable() throws CloneNotSupportedException {
        /* schema.sql lists 5 theaters, generate 2 screenings randomly for
         * each screen in each theater
         */

        if (screenRepository.count() > SCREEN_DEFAULT_SIZE) return;

        for (int i = 1; i <= 5; i++) {
            List<Screen> screens = screenRepository.findByTheatreId(i);
            for (int j = 1; j < screens.size() + 1; j++) {
                Screening screening = ScreeningFactory.makeRandom();

                Integer screenId = j;
                Integer theaterId = i;
                String movieName = randomMovie().getMovieName();

                screening.setTheatreId(theaterId);
                screening.setScreenId(screenId);
                screening.setMovieName(movieName);
                screeningRepository.save(screening);

                Screening screeningClone = ScreeningFactory.shuffledClone(screening);
                screeningRepository.save(screeningClone);

                shuffleInformationAndSave(screening);
            }
        }
    }

    private void shuffleInformationAndSave(Screening source) {
        Screening screeningToShuffle = ScreeningFactory.clone(source);
        Date sourceDate = screeningToShuffle.getScreeningDate();
        Date newDate = DateUtils.plusDays(sourceDate, 1);
        screeningToShuffle.setScreeningDate(newDate);
        screeningRepository.save(screeningToShuffle);

        Screening shuffledClone = ScreeningFactory.shuffledClone(screeningToShuffle);
        screeningRepository.save(shuffledClone);
    }

    private Movie randomMovie() {
        return movieRepository.findRandom();
    }

    class ProcessMovie implements Runnable {
        private String movieLine;
        private String linkLine;

        ProcessMovie(String movieLine, String linkLine) {
            this.movieLine = movieLine;
            this.linkLine = linkLine;
        }

        @Override
        public void run() {
            LOGGER.info(Thread.currentThread().getId() + ":" + linkLine);
            String[] movieInfo = movieLine.split(",");

            String movieName = "";

            for (int i = 1; i < movieInfo.length - 1; i++) {
                if (i == movieInfo.length - 2)
                    movieName += movieInfo[i];
                else
                    movieName += movieInfo[i] + ",";
            }

            Movie movie = new Movie();
            movie.setMovieId(Long.parseLong(movieInfo[0]));
            movie.setMovieName(movieName.substring(0, movieName.indexOf('(')).trim());
            movie.setMovieTags(movieInfo[2]);

            String[] linkInfo = linkLine.split(",");
            Document movieLensPage = null;
            try {
                movieLensPage = Jsoup.connect("https://www.imdb.com/title/tt" + linkInfo[1]).get();
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