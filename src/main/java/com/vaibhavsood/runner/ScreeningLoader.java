package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Screen;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreenRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScreeningLoader implements TableLoader {

    private final ScreeningRepository screeningRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;

    public ScreeningLoader(ScreeningRepository screeningRepository, ScreenRepository screenRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.screenRepository = screenRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public void populate() {
        if (screeningRepository.count() > 0L) return;
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
        screening.setMovieName(randomMovieName());
        shuffleAndSaveMultipleTimes(screening);
    }

    private String randomMovieName() {
        return movieRepository.findRandom().getMovieName();
    }

    private void shuffleAndSaveMultipleTimes(Screening screening) {
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
}
