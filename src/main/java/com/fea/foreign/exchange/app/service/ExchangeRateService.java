package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.exceptions.IllegalParamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;

import static com.fea.foreign.exchange.app.constants.General.CURRENCY;

@Service
public class ExchangeRateService {
    private final HttpConnectionService httpConnection;

    @Autowired
    public ExchangeRateService(HttpConnectionService httpConnection) {
        this.httpConnection = httpConnection;
    }

    //May hold methods concerning exchange rate operations: fetch by single currency target, multiple currency targets, historical exchange rates, etc.

    public BigDecimal fetchFxRateBySourceAndTarget(String source, String target) throws IOException {
        validateCurrency(source, target);
        return httpConnection.requestFXRate(source,target);
    }

    private void validateCurrency(String... args) throws IOException {
        HashSet<String> availableCurrencies = httpConnection.requestCurrencies();

        for (String currency : args) {
            if (!availableCurrencies.contains(currency)){
                throw new IllegalParamException(CURRENCY);
            }
        }
    }
}
