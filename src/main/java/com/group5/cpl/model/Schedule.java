package com.group5.cpl.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long schedule_id;

    private Integer value; // 600 = 10:00    630 = 10:30

    @OneToMany(mappedBy = "schedule")
    private List<Movie_Schedule> scheduleList;

    public Schedule(Integer value) {
        this.value = value;
    }
}
