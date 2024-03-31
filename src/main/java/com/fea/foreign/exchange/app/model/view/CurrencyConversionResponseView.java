package com.fea.foreign.exchange.app.model.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class CurrencyConversionResponseView {
    private BigDecimal convertedAmount;
    private UUID transactionID;
}
