package com.vaibhavsood.data.repository;

import com.vaibhavsood.data.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends CrudRepository<Movie, String> {
    Movie findByMovieName(String movieName);
    Movie findByMovieId(long movieId);
    @Query(value = "SELECT * FROM MOVIE ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Movie findRandom();
    List<Movie> findAll();
}
