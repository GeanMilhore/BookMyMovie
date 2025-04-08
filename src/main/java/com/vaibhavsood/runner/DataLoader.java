package com.vaibhavsood.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final MovieLoader movieLoader;
    private final ScreeningLoader screeningLoader;

    public DataLoader(MovieLoader movieLoader, ScreeningLoader screeningLoader) {
        this.movieLoader = movieLoader;
        this.screeningLoader = screeningLoader;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        movieLoader.populate();
        screeningLoader.populate();
    }
}