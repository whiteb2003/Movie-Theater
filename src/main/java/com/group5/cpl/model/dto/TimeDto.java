package com.group5.cpl.model.dto;

import com.group5.cpl.model.Movie_Schedule;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TimeDto {
    private String time;

    private Movie_Schedule movie_schedule;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Movie_Schedule getMovie_schedule() {
        return movie_schedule;
    }

    public void setMovie_schedule(Movie_Schedule movie_schedule) {
        this.movie_schedule = movie_schedule;
    }
}
