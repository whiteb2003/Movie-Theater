package com.group5.cpl.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie_schedule")
public class Movie_Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_schedule_id")
    private Long movie_schedule_id;

    @ManyToOne
    @JoinColumn(name = "mrd_id", referencedColumnName = "mrd_id")
    private Movie_Room_Date mrd_item;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
    private Schedule schedule;

    @OneToMany(mappedBy = "movie_schedule")
    private List<Ticket> ticketList;
}
