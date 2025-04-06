package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.utils.DateUtils;

import java.sql.Date;
import java.sql.Time;
import java.util.concurrent.ThreadLocalRandom;

public class ScreeningFactory {

    public static Screening clone(Screening source) {
        try {
            Screening clone = (Screening) source.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Screening shuffledClone(Screening source){
        try {
            Screening clone = (Screening) source.clone();
            shuffleTime(clone);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Screening makeRandom() {
        Screening newScreening = new Screening();
        newScreening.setBookedTickets(0);
        newScreening.setScreeningDate(createThreeDaysDateRange());
        newScreening.setScreeningTime(Time.valueOf("10:00:00"));
        return newScreening;
    }

    private static Date createThreeDaysDateRange() {
        Date today = DateUtils.now();
        Date future = DateUtils.fromCurrentDatePlusDays(3);
        return createThreadThreeDaysDateRange(today, future);
    }

    private static Date createThreadThreeDaysDateRange(Date today, Date future) {
        ThreadLocalRandom currentThread = ThreadLocalRandom.current();
        long randomTimeBetween = currentThread.nextLong(today.getTime(), future.getTime());
        return new Date(randomTimeBetween);
    }

    // Todo - Create a Screening Shuffler
    private static void shuffleTime(Screening source){
        Time sourceTime = source.getScreeningTime();
        if(sourceTime.toString().equals("10:00:00")){
            source.setScreeningTime(Time.valueOf("18:00:00"));
            return;
        }

        source.setScreeningTime(Time.valueOf("10:00:00"));
    }
}
