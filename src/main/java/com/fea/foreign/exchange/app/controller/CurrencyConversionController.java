package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.fea.foreign.exchange.app.constants.Endpoints.*;

@RestController
@RequestMapping(API + CONVERSION)
public class CurrencyConversionController {
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public CurrencyConversionController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @PostMapping(CONVERT)
    public ResponseEntity<CurrencyConversionResponseView> convertCurrency(@RequestBody CurrencyConversionRequestDTO currencyConversionRequestDTO) throws IOException {
        CurrencyConversionResponseView response = this.currencyConversionService.convertCurrency(currencyConversionRequestDTO);

        return ResponseEntity.ok(response);
    }
}
