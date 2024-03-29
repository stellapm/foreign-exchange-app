package com.fea.foreign.exchange.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "currency_conversions")
@NoArgsConstructor
@Getter
@Setter
public class CurrencyConversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_currency")
    private String sourceCurrency;

    @Column(name = "source_amount")
    private BigDecimal sourceAmount;

    @Column(name = "target_currency")
    private String targetCurrency;

    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @Column(name = "transaction_id", unique = true)
    private UUID transactionID;

    @Column(name = "transaction_timestamp")
    private LocalDateTime transactionTimeStamp;
}
