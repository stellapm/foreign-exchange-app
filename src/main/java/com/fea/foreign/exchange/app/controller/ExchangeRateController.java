package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

import static com.fea.foreign.exchange.app.constants.Endpoints.*;

@RestController
@RequestMapping(API + FX_RATE)
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping(GET)
    public ResponseEntity<BigDecimal> getExchangeRate(@RequestParam String sourceCurrency,
                                                  @RequestParam String targetCurrency) throws IOException {
        BigDecimal exchangeRate = this.exchangeRateService.fetchFxRateBySourceAndTarget(sourceCurrency, targetCurrency);

        System.out.println(exchangeRate);

        return ResponseEntity.ok(exchangeRate);
    }
}
