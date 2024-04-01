package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionInfoView;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static com.fea.foreign.exchange.app.constants.Endpoints.*;
import static com.fea.foreign.exchange.app.constants.General.DEFAULT_PAGE_SIZE;

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

    @GetMapping(HISTORY)
    public ResponseEntity<Page<CurrencyConversionInfoView>> getConversionHistory(
            @RequestParam(required = false) UUID transactionID,
            @RequestParam(required = false) LocalDate transactionDate,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable){
        Page<CurrencyConversionInfoView> conversionHistory = this.currencyConversionService.getConversionHistory(transactionID, transactionDate, pageable);

        return ResponseEntity.ok(conversionHistory);
    }
}
