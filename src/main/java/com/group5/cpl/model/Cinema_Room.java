package com.group5.cpl.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cinema_room")
public class Cinema_Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long room_id;

    @Column(name = "room_name")
    private String room_name;

    @Column(name = "seat_quantity")
    private Integer seat_quantity;

    @Column(name = "seat_column")
    private Integer seat_column;

    @Column(name = "seat_row")
    private Integer seat_row;

    //presumably the room has the seats arranged in the m x n format, another proposed case is the use of a two dimensional array to store the seat position
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private List<Seat> seatList;

    @OneToMany(mappedBy = "room")
    private List<Movie_Room_Date> scheduleList;

    public Long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Long room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public Integer getSeat_quantity() {
        return seat_quantity;
    }

    public void setSeat_quantity(Integer seat_quantity) {
        this.seat_quantity = seat_quantity;
    }

    public Integer getSeat_column() {
        return seat_column;
    }

    public void setSeat_column(Integer seat_column) {
        this.seat_column = seat_column;
    }

    public Integer getSeat_row() {
        return seat_row;
    }

    public void setSeat_row(Integer seat_row) {
        this.seat_row = seat_row;
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    public List<Movie_Room_Date> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Movie_Room_Date> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
