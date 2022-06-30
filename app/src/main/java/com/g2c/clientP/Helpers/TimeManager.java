package com.g2c.clientP.Helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeManager {
    Calendar calendar;
    String[] timeArray, dateArray;
    SimpleDateFormat timeFormat, timeUsFormat;
    SimpleDateFormat dateFormat, dateUsFormat;
    Long currentTimeStamp;


    public TimeManager() {
        calendar = Calendar.getInstance();
        currentTimeStamp = calendar.getTime().getTime();
        timeFormat = new SimpleDateFormat("hh:mm a");
        timeUsFormat = new SimpleDateFormat("kk_mm");
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateUsFormat = new SimpleDateFormat("dd_MM_yyyy");

    }


    public Long getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    public void setCurrentTimeStamp(Long currentTimeStamp) {
        this.currentTimeStamp = currentTimeStamp;
    }

    public SimpleDateFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(SimpleDateFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Long getCurrentDayTimeStamp(String time) {
        timeArray = time.split("_");

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(timeArray[1]));

        return calendar.getTime().getTime();
    }

    public Long getSpecificDayTimeStamp(String date, String time) {
        timeArray = time.split("_");
        dateArray = date.split("_");

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(timeArray[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateArray[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateArray[1]) - 1);
        calendar.set(Calendar.YEAR, Integer.valueOf(dateArray[2]));


        return calendar.getTime().getTime();


    }

    public Long getSpecificDayTimeStamp(String date) {
        dateArray = date.split("_");

        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateArray[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateArray[1]) - 1);
        calendar.set(Calendar.YEAR, Integer.valueOf(dateArray[2]));


        return calendar.getTime().getTime();


    }

    public Long checkCurrentDaySlot(Long slot, Integer hr) {

        Calendar c = Calendar.getInstance();
        Date d = new Date();
        d.setTime(slot);
        c.setTime(d);

        c.add(Calendar.HOUR_OF_DAY, hr * (-1));

        return c.getTime().getTime();

    }

    public String getUsDate(Long t) {

        return dateUsFormat.format(t);

    }

    public String getUsTime(Long t) {

        return timeUsFormat.format(t);

    }


}
