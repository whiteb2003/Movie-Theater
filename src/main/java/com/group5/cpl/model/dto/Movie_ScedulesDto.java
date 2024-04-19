package com.group5.cpl.model.dto;

import java.sql.Date;

public class Movie_ScedulesDto {
    long movie_schedule_id;
    String movie_name;
    Date movie_date;
    String schedule;
    String room_name;


    public long getMovie_schedule_id() {
        return movie_schedule_id;
    }

    public void setMovie_schedule_id(long movie_schedule_id) {
        this.movie_schedule_id = movie_schedule_id;
    }
    public Movie_ScedulesDto() {
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public Date getMovie_date() {
        return movie_date;
    }

    public void setMovie_date(Date movie_date) {
        this.movie_date = movie_date;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }
}
