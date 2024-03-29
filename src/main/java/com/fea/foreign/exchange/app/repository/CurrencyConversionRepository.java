package com.fea.foreign.exchange.app.repository;

import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {
}
