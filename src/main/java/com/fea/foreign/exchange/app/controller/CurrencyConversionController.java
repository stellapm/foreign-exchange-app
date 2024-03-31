package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fea.foreign.exchange.app.constants.Endpoints.*;

@RestController
@RequestMapping(API + CONVERSION)
public class CurrencyConversionController {
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public CurrencyConversionController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @GetMapping(GET)
    public ResponseEntity<CurrencyConversionResponseView> getCurrencyConversion(@RequestBody CurrencyConversionRequestDTO currencyConversionRequestDTO){
        return ResponseEntity.ok(new CurrencyConversionResponseView());
    }
}
