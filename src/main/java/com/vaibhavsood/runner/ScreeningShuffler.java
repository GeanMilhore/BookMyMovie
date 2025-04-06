package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.utils.DateUtils;

import java.sql.Date;
import java.sql.Time;

public class ScreeningShuffler {

    private Screening target;

    public ScreeningShuffler(Screening target){
        this.target = ScreeningFactory.clone(target);
    }

    public Screening cloneAndIncreaseDaysBy(Integer days){
        Date sourceDate = target.getScreeningDate();
        Date newDate = DateUtils.plusDays(sourceDate, days);
        target.setScreeningDate(newDate);
        return ScreeningFactory.clone(target);
    }

    public Screening cloneAndShuffleTime(){
        Time sourceTime = target.getScreeningTime();
        if(sourceTime.toString().equals("10:00:00"))
            target.setScreeningTime(Time.valueOf("18:00:00"));
        else
            target.setScreeningTime(Time.valueOf("10:00:00"));
        return ScreeningFactory.clone(target);
    }
}