package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

import static com.fea.foreign.exchange.app.constants.Endpoints.API;
import static com.fea.foreign.exchange.app.constants.Endpoints.FX_RATE;

@RestController
@RequestMapping(API)
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping(FX_RATE)
    public ResponseEntity<BigDecimal> getExchangeRate(@RequestParam String sourceCurrency,
                                                  @RequestParam String targetCurrency) throws IOException {
        BigDecimal exchangeRate = this.exchangeRateService.fetchFxRateBySourceAndTarget(sourceCurrency, targetCurrency);

        return ResponseEntity.ok(exchangeRate);
    }
}
