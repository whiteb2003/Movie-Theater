package com.group5.cpl.model;

import com.group5.cpl.model.enums.TicketStatus;
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
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticket_id;

    @ManyToOne
    @JoinColumn(name = "Seat_id", referencedColumnName = "Seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "movie_schedule_id", referencedColumnName = "movie_schedule_id")
    private Movie_Schedule movie_schedule;

    @ManyToOne
    @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id")
    private Invoice invoice;

//    @Enumerated(EnumType.STRING)
    private TicketStatus status;
//    public void setStatus(TicketStatus status) {
//        if (status != TicketStatus.PENDING &&
//                status != TicketStatus.DROPPED &&
//                status != TicketStatus.PAID &&
//                status != TicketStatus.CANCEL) {
//            throw new IllegalArgumentException("Invalid ticket status");
//        }
//        this.status = status;
//    }
}
