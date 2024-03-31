package com.fea.foreign.exchange.app.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CurrencyConversionRequestDTO {
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private String targetCurrency;
}
