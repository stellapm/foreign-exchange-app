package com.fea.foreign.exchange.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CurrencyConversionRequestDTO {
    @Positive
    private BigDecimal sourceAmount;

    @NotBlank
    private String sourceCurrency;

    @NotBlank
    private String targetCurrency;
}
