package com.vaibhavsood.utils;

import java.sql.Date;
import java.time.LocalDate;

public class DateUtils {

    public static Date now(){
        return Date.valueOf(LocalDate.now());
    }

    public static Date plusDays(Date source, Integer days){
        LocalDate localDate = source.toLocalDate().plusDays(days);
        return Date.valueOf(localDate);
    }

    public static Date fromCurrentDatePlusDays(Integer plusDays) {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(plusDays);
        return Date.valueOf(future);
    }
}
