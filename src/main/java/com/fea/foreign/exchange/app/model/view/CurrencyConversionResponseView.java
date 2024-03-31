package com.fea.foreign.exchange.app.model.view;

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
    private UUID transactionID;
    private BigDecimal convertedAmount;
}
