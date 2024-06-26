package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.controller.GlobalExceptionHandler;
import com.fea.foreign.exchange.app.exceptions.IllegalParamException;
import com.fea.foreign.exchange.app.exceptions.ObjectNotFoundException;
import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import com.fea.foreign.exchange.app.model.mapper.CurrencyConversionMapper;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionInfoView;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.repository.CurrencyConversionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.fea.foreign.exchange.app.constants.General.TRANSACTION_DATE;
import static com.fea.foreign.exchange.app.constants.General.TRANSACTION_ID;

@Service
public class CurrencyConversionService {
    private final CurrencyConversionRepository repository;
    private final ExchangeRateService exchangeRateService;
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


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

    @Cacheable(value = "conversionHistoryCache", key = "#transactionID?.toString() + #transactionDate?.toString() + #pageable?.pageNumber + #pageable?.pageSize")
    public Page<CurrencyConversionInfoView> getConversionHistory(UUID transactionID, LocalDate transactionDate, Pageable pageable) {
        logger.info("Fetching conversion history from database by transactionID and transactionDate");

        Page<CurrencyConversion> filteredConversionsPage = filterByTransactionIDOrDate(transactionID, transactionDate, pageable);

        checkIfPageableResultIsPresent(filteredConversionsPage);


        return filteredConversionsPage
                .map(CurrencyConversionMapper.INSTANCE::conversionToConversionInfoView);
    }

    private void checkIfPageableResultIsPresent(Page<?> pageableResult) {
        if (pageableResult.isEmpty()){
            throw new ObjectNotFoundException();
        }
    }

    private Page<CurrencyConversion> filterByTransactionIDOrDate(UUID transactionID, LocalDate transactionDate, Pageable pageable) {
        verifyBothArgumentsNotNull(transactionID, transactionDate);

        if (transactionID == null) {
            return this.repository.findByTransactionTimeStamp(transactionDate, pageable);
        } else if (transactionDate == null) {
            return this.repository.findByTransactionID(transactionID, pageable);
        } else {
            return this.repository.findByTransactionIdAndTransactionDate(transactionID, transactionDate, pageable);
        }
    }

    private void verifyBothArgumentsNotNull(UUID transactionID, LocalDate transactionDate) {
        if (transactionID == null && transactionDate == null){
            throw new IllegalParamException(TRANSACTION_ID + ", " + TRANSACTION_DATE);
        }
    }
}
