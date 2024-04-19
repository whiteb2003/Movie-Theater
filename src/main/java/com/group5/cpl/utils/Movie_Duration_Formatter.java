package com.group5.cpl.utils;

import org.springframework.stereotype.Service;

@Service
public class Movie_Duration_Formatter {
    public String movieTime_AMPM(Integer timemin) {
        String AMPM = "";
        String time = "";
        if (timemin / 60 > 12) {
            AMPM = "PM";
            time += (timemin / 60) - 12;
        } else {
            AMPM = "AM";
            time += (timemin / 60);
        }

        time += ":";
        int min = timemin % 60;
        if (min < 10) {
            time = time + "0" + min;
        } else {
            time += min;
        }
        time += AMPM;
        return time;
    }

    public String movieTime_24H(Integer timemin) {
        String time = "";
        time += timemin / 60;
        time += ":";
        int min = timemin % 60;
        if (min < 10) {
            time = time + "0" + min;
        } else {
            time += min;
        }
        return time;
    }

    public String durationformat(Integer timemin) {
        String time = "";
        time += timemin / 60;
        time += " hours ";
        int min = timemin % 60;
        if (min < 10) {
            time = time + "0" + min + " mins";
        } else if (min != 0) {
            time += min + " mins";
        }
        return time;
    }

    public int getMinutesFromString(String timeString) {
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}
