package com.fea.foreign.exchange.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Amount to be converted", example = "100.00")
    private BigDecimal sourceAmount;

    @NotBlank
    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @NotBlank
    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;
}
