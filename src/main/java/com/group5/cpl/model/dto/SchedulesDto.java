package com.group5.cpl.model.dto;

import lombok.Data;

@Data
public class SchedulesDto {
    String start;
    String end;
    String room_name;
    int seat_booked;
    int seat;
    long movie_schedules_id;
}
