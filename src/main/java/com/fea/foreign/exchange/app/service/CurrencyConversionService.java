package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.repository.CurrencyConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {
    private final CurrencyConversionRepository repository;

    @Autowired
    public CurrencyConversionService(CurrencyConversionRepository repository) {
        this.repository = repository;
    }
}
