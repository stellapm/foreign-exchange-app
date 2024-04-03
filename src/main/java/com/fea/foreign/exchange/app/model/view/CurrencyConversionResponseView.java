package com.fea.foreign.exchange.app.model.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CurrencyConversionResponseView {
    @Schema(description = "Unique ID created", example = "1c0f759a-baf1-45d5-8b72-d5c3a5d3e500")
    private UUID transactionID;

    @Schema(description = "Amount after conversion", example = "92.879")
    private BigDecimal convertedAmount;
}
