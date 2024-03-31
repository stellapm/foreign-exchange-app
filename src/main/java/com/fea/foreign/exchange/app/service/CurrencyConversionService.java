package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import com.fea.foreign.exchange.app.model.mapper.CurrencyConversionMapper;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.repository.CurrencyConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CurrencyConversionService {
    private final CurrencyConversionRepository repository;
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public CurrencyConversionService(CurrencyConversionRepository repository, ExchangeRateService exchangeRateService) {
        this.repository = repository;
        this.exchangeRateService = exchangeRateService;
    }

    public CurrencyConversionResponseView convertCurrency(CurrencyConversionRequestDTO request) throws IOException {
        BigDecimal exchangeRate = this.exchangeRateService.fetchFxRateBySourceAndTarget(request.getSourceCurrency(), request.getTargetCurrency());
        BigDecimal targetAmount = calculateExchange(request.getSourceAmount(), exchangeRate);

        CurrencyConversion conversion = saveConversionTransaction(request, targetAmount);

        return getCurrencyConversionResponseView(conversion.getTransactionID(), targetAmount);
    }

    private CurrencyConversionResponseView getCurrencyConversionResponseView(UUID transactionId, BigDecimal targetAmount) {
        return new CurrencyConversionResponseView(transactionId, targetAmount);
    }

    private CurrencyConversion saveConversionTransaction(CurrencyConversionRequestDTO request, BigDecimal targetAmount) {
        CurrencyConversion conversion = CurrencyConversionMapper.INSTANCE.conversionRequestDTOtoCurrencyConversion(request);

        conversion.setTargetAmount(targetAmount);
        conversion.setTransactionID(UUID.randomUUID());
        conversion.setTransactionTimeStamp(LocalDateTime.now());

        this.repository.save(conversion);

        return conversion;
    }

    private BigDecimal calculateExchange(BigDecimal sourceAmount, BigDecimal exchangeRate) {
        return sourceAmount.multiply(exchangeRate);
    }
}
