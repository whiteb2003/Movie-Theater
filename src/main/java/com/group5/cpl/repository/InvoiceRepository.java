package com.group5.cpl.repository;

import com.group5.cpl.model.Invoice;
import com.group5.cpl.model.Movie_Room_Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

        @Query("SELECT i from Invoice i where i.user_id = ?1")
        Page<Invoice> findListInvoicesByUserId(Long userId, Pageable pageable);

        @Query("SELECT i from Invoice i where i.user_id = ?1 and i.invoice_id = ?2")
        Invoice findInvoiceInfoByUserId(Long userId, Long invoiceId);

        Page<Invoice> findAll(Pageable pageable);

        @Query("SELECT i FROM Invoice i WHERE LOWER(i.code) LIKE LOWER(concat('%', :code, '%')) OR :code IS NULL OR :code = '' ORDER BY i.invoice_id DESC")
        Page<Invoice> findInvoicesByCode(@Param("code") String code, Pageable pageable);

        @Query("select i from Invoice i " +
                "where i.user_id = :userId " +
                "and (:code is null or :code = '' or i.code = :code) " +
                "and (:price is null or :price = 0 or i.price = :price) ")
        List<Invoice> getListInvoice(@Param("userId") Long userId,
                                     @Param("code") String code,
                                     @Param("price") Double price);



        @Query("SELECT i from Invoice i where i.code = ?1")
        Invoice findInvoiceByCode(String code);
}
