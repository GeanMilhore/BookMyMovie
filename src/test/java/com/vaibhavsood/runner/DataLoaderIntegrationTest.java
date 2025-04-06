package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Time;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DataLoaderIntegrationTest {
    @Autowired
    private DataLoader dataLoader;

    @BeforeAll
    public void setUp() throws Exception {
        try {
            dataLoader.run(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVerifyDataBaseWasPopulated(){
        List<Screening> allScreenings = dataLoader.getScreeningRepository().findAll();
        List<Movie> allMovies = dataLoader.getMovieRepository().findAll();

        assertFalse(allMovies.isEmpty());
        assertFalse(allScreenings.isEmpty());
    }

    @Test
    public void testFindMovieByName() {
        MovieRepository movieRepository = dataLoader.getMovieRepository();

        Movie foundMovie = movieRepository.findByMovieName("Toy Story");
        assertNotNull(foundMovie);
        assertEquals(foundMovie.getMovieName(), "Toy Story");
        assertEquals("imdb.com/title/tt0114709/mediaviewer/rm3813007616/?ref_=tt_ov_i", foundMovie.getMoviePosterUrl());
    }

    @Test
    public void testFindScreeningByMovieName(){
        ScreeningRepository repository = dataLoader.getScreeningRepository();
        List<Screening> screeningList = repository.findAll();
        Screening firstScreening = screeningList.get(0);

        assertEquals(firstScreening.getScreeningId(), 1);
        assertEquals(firstScreening.getScreeningTime(), Time.valueOf("18:00:00"));
        assertFalse(firstScreening.getMovieName().isEmpty());
        assertNotNull(firstScreening.getScreenId());
    }
}