package com.group5.cpl.model;

import com.group5.cpl.model.enums.InvoiceStatus;
import com.group5.cpl.model.enums.PromotionStatus;
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
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoice_id;

    @OneToMany(mappedBy = "invoice")
    private List<Ticket> ticketList;

    //promotion
    @ManyToOne()
    @JoinColumn(name = "promotion_id", referencedColumnName = "promotion_id")
    private Promotion promotion;

    @Column(name = "price", columnDefinition = "DOUBLE")
    private Double price;

    private Long user_id;

    private String code;

    private Boolean usePoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    private InvoiceStatus status;

    private String qr;
}
