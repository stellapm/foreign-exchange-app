package com.fea.foreign.exchange.app.model.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class CurrencyConversionInfoView {
    private Long id;
    private String sourceCurrency;
    private BigDecimal sourceAmount;
    private String targetCurrency;
    private BigDecimal targetAmount;
    private UUID transactionID;
    private LocalDate transactionDate;
}
