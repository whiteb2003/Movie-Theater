package com.group5.cpl.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group5.cpl.model.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Room_Seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Seat_id")
    private Long id;
    @Column(name = "Seat_Status")
    private boolean status;
    @Column(name = "Seat_Type")
    private SeatType type;
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id")
    @JsonIgnore
    private Cinema_Room room;
    @Column(name = "seat_position")
    private String position; //format G2 H9 A12
    @OneToMany(mappedBy = "seat")
    private List<Ticket> ticketList;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

    public Cinema_Room getRoom() {
        return room;
    }

    public void setRoom(Cinema_Room room) {
        this.room = room;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}