package com.group5.cpl.model;

import com.group5.cpl.model.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotion_id;

    @Column(name = "title", columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "discount_value", columnDefinition = "DECIMAL(10,2)")
    private Double discountValue; // minus $XX

    @Column(name = "discount_percentage", columnDefinition = "DECIMAL(5,2)")
    private Double discountPercentage; //30%

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    private PromotionStatus status;

    @Column(name = "start_date", columnDefinition = "DATE")
    private Date startDate;

    @Column(name = "end_date", columnDefinition = "DATE")
    private Date endDate;

    @Column(name = "image", columnDefinition = "VARCHAR(255)")
    private String image;

    @OneToMany(mappedBy = "promotion")
    private List<Invoice> invoices;

}
