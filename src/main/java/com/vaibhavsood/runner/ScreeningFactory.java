package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Screen;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.utils.DateUtils;

import java.sql.Date;
import java.sql.Time;
import java.util.concurrent.ThreadLocalRandom;

public class ScreeningFactory {

    public static Screening clone(Screening source) {
        try {
            return cloneScreening(source);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Screening cloneScreening(Screening source) throws CloneNotSupportedException {
        Screening clone = (Screening) source.clone();
        clone.setScreeningId(0);
        return clone;
    }

    public static Screening makeRandom(Screen session) {
        Screening newScreening = new Screening();
        newScreening.setBookedTickets(0);
        newScreening.setScreeningDate(createThreeDaysDateRange());
        newScreening.setScreeningTime(Time.valueOf("10:00:00"));
        newScreening.setTheatreId(session.getTheatreId());
        newScreening.setScreenId(session.getScreenId());
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
}
