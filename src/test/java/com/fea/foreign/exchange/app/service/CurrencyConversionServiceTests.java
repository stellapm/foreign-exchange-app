package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.repository.CurrencyConversionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyConversionServiceTests {
    @Mock
    private CurrencyConversionRepository mockRepository;

    @Mock
    private ExchangeRateService mockExchangeService;

    @InjectMocks
    private CurrencyConversionService testService;

    private BigDecimal mockedExchangeRate;
    private CurrencyConversionRequestDTO requestDTO;

    @BeforeEach
    public void setup() throws IOException {
        this.testService = new CurrencyConversionService(mockRepository, mockExchangeService);

        requestDTO = new CurrencyConversionRequestDTO();
        requestDTO.setSourceCurrency("USD");
        requestDTO.setTargetCurrency("EUR");
        requestDTO.setSourceAmount(new BigDecimal("100"));

        this.mockedExchangeRate = new BigDecimal("0.85");
        when(mockExchangeService.fetchFxRateBySourceAndTarget(anyString(), anyString())).thenReturn(this.mockedExchangeRate);
    }

    @Test
    public void testConvertCurrencyReturnsValidObject() throws IOException {
        CurrencyConversionResponseView testConversion = this.testService.convertCurrency(requestDTO);

        assertNotNull(testConversion, "Response should not be null");
    }

    @Test
    public void testConvertCurrencyReturnsCorrectAmountAndTransactionID() throws IOException {
        CurrencyConversionResponseView testConversion = this.testService.convertCurrency(requestDTO);

        assertEquals(requestDTO.getSourceAmount().multiply(mockedExchangeRate), testConversion.getConvertedAmount(), "The target amount should be correctly calculated based on the exchange rate");
        assertNotNull(testConversion.getTransactionID(), "Transaction ID should not be null");

    }

    @Test
    public void testConvertCurrencySavesProperlyCurrencyConversionToDatabase() throws IOException {
        this.testService.convertCurrency(requestDTO);
        verify(this.mockRepository, times(1)).save(any(CurrencyConversion.class));
    }

    @Test
    public void testConvertCurrencyThrowsExceptionInvalidCurrencies() throws IOException {
        requestDTO.setSourceCurrency("INVALID");

        when(mockExchangeService.fetchFxRateBySourceAndTarget(anyString(), anyString()))
                .thenThrow(new IOException("Failed to fetch result."));

        Throwable failedFetch = assertThrows(IOException.class, () -> testService.convertCurrency(requestDTO),
                "Should throw IOException for invalid currency codes");

        assertEquals("Failed to fetch result.", failedFetch.getMessage());
    }
}
