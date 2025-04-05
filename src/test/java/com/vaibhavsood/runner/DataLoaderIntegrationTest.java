package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
    public void testPopulateMovieTable() {
        MovieRepository movieRepository = dataLoader.getMovieRepository();

        assertNotNull(movieRepository.findByMovieName("Toy Story"));
        assertEquals(movieRepository.findByMovieName("Toy Story").getMovieName(), "Toy Story");
        assertEquals("imdb.com/title/tt0114709/mediaviewer/rm3813007616/?ref_=tt_ov_i", movieRepository.findByMovieName("Toy Story").getMoviePosterUrl());
    }

    @Test
    public void testPopulateScreeningsTable() {
        ScreeningRepository screeningRepository = dataLoader.getScreeningRepository();

        List<Screening> screenings = screeningRepository.findAll();
        assertNotEquals(screenings.size(), 0);
    }
}