package com.fea.foreign.exchange.app.repository;

import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {
    Page<CurrencyConversion> findByTransactionID(UUID transactionID, Pageable pageable);

    @Query("SELECT c FROM CurrencyConversion c " +
            "WHERE CAST(c.transactionTimeStamp AS DATE) = :transactionDate")
    Page<CurrencyConversion> findByTransactionTimeStamp(LocalDate transactionDate, Pageable pageable);
    @Query("SELECT c FROM CurrencyConversion c " +
            "WHERE c.transactionID = :transactionID AND " +
            "CAST(c.transactionTimeStamp AS DATE) = :transactionDate")
    Page<CurrencyConversion> findByTransactionIdAndTransactionDate(UUID transactionID, LocalDate transactionDate, Pageable pageable);
}
