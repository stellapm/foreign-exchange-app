package com.fea.foreign.exchange.app.service;
import com.fea.foreign.exchange.app.exceptions.IllegalParamException;
import com.fea.foreign.exchange.app.exceptions.ObjectNotFoundException;
import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import com.fea.foreign.exchange.app.model.mapper.CurrencyConversionMapper;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionInfoView;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.repository.CurrencyConversionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Mock
    private CurrencyConversionMapper mockMapper;

    @InjectMocks
    private CurrencyConversionService testService;

    private BigDecimal mockedExchangeRate;
    private CurrencyConversionRequestDTO requestDTO;
    private Pageable pageable;
    private LocalDate transactionDate;
    private UUID transactionID;
    private Page<CurrencyConversion> mockPageFull;
    private CurrencyConversion conversion1;
    private CurrencyConversion conversion2;
    private CurrencyConversion conversion3;


    @BeforeEach
    public void setup() {
        this.testService = new CurrencyConversionService(mockRepository, mockExchangeService);

        requestDTO = new CurrencyConversionRequestDTO();
        requestDTO.setSourceCurrency("USD");
        requestDTO.setTargetCurrency("EUR");
        requestDTO.setSourceAmount(new BigDecimal("100"));

        this.mockedExchangeRate = new BigDecimal("0.85");

        this.transactionDate = LocalDate.now();
        this.transactionID = UUID.randomUUID();

        currencyConversionSetup();

        this.mockPageFull = new PageImpl<>(List.of(this.conversion1, this.conversion2, this.conversion3));
    }

    private void currencyConversionSetup() {
        this.conversion1 = new CurrencyConversion(1L,
                "USD", BigDecimal.valueOf(6.66),
                "BGN", BigDecimal.valueOf(3.63),
                this.transactionID,
                LocalDateTime.now());

        this.conversion2 = new CurrencyConversion(1L,
                "USD", BigDecimal.valueOf(3.33),
                "EUR", BigDecimal.valueOf(4.63),
                UUID.randomUUID(),
                LocalDateTime.now());

        this.conversion3 = new CurrencyConversion(1L,
                "UYU", BigDecimal.valueOf(9.99),
                "BGN", BigDecimal.valueOf(5.63),
                UUID.randomUUID(),
                LocalDateTime.now());
    }

    @Test
    public void testConvertCurrencyReturnsValidObject() throws IOException {
        when(mockExchangeService.fetchFxRateBySourceAndTarget(anyString(), anyString())).thenReturn(this.mockedExchangeRate);

        CurrencyConversionResponseView testConversion = this.testService.convertCurrency(requestDTO);

        assertNotNull(testConversion, "Response should not be null");
    }

    @Test
    public void testConvertCurrencyReturnsCorrectAmountAndTransactionID() throws IOException {
        when(mockExchangeService.fetchFxRateBySourceAndTarget(anyString(), anyString())).thenReturn(this.mockedExchangeRate);
        CurrencyConversionResponseView testConversion = this.testService.convertCurrency(requestDTO);

        assertEquals(requestDTO.getSourceAmount().multiply(mockedExchangeRate), testConversion.getConvertedAmount(),
                "The target amount should be correctly calculated based on the exchange rate");
        assertNotNull(testConversion.getTransactionID(), "Transaction ID should not be null");

    }

    @Test
    public void testConvertCurrencySavesProperlyCurrencyConversionToDatabase() throws IOException {
        when(mockExchangeService.fetchFxRateBySourceAndTarget(anyString(), anyString())).thenReturn(this.mockedExchangeRate);
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

    @Test
    public void testGetConversionHistoryThrowsExceptionOnMissingParams() {
        Throwable failedFetch = assertThrows(IllegalParamException.class, () -> testService.getConversionHistory(null, null, this.pageable),
                "Should throw IllegalParamException if both transactionID and transactionDate are null");

        assertEquals("Please make sure all requested parameters are provided: Transaction ID, Transaction Date",
                failedFetch.getMessage());
    }


    @Test
    public void testGetConversionHistoryByValidTransactionDate(){
        when(mockRepository.findByTransactionTimeStamp(this.transactionDate, this.pageable)).thenReturn(this.mockPageFull);

        Page<CurrencyConversionInfoView> results = this.testService.getConversionHistory(null, this.transactionDate, this.pageable);

        assertEquals(3, results.getTotalElements(), "Page should contain correct amount of fetched transactions");
        assertEquals(results.getContent().get(0).getTransactionID(), conversion1.getTransactionID(), "Page should contain correct item");
    }

    @Test
    public void testGetConversionHistoryByValidTransactionID(){
        when(mockRepository.findByTransactionID(this.transactionID, this.pageable)).thenReturn(new PageImpl<>(List.of(this.conversion1)));

        Page<CurrencyConversionInfoView> results = this.testService.getConversionHistory(this.transactionID, null, this.pageable);

        assertEquals(1, results.getTotalElements(), "Page should contain correct amount of fetched transactions");
        assertEquals(results.getContent().get(0).getTransactionID(), conversion1.getTransactionID(), "Page should contain correct item");
    }

    @Test
    public void testGetConversionHistoryByValidTransactionIDAndTransactionDate(){
        when(mockRepository.findByTransactionIdAndTransactionDate(this.transactionID, this.transactionDate, this.pageable)).thenReturn(new PageImpl<>(List.of(this.conversion1)));

        Page<CurrencyConversionInfoView> results = this.testService.getConversionHistory(this.transactionID, this.transactionDate, this.pageable);

        assertEquals(1, results.getTotalElements(), "Page should contain correct amount of fetched transactions");
        assertEquals(results.getContent().get(0).getTransactionID(), conversion1.getTransactionID(), "Page should contain correct item");
    }

    @Test
    public void testGetConversionHistoryByValidTransactionIDAndTransactionDateNoResults(){
        UUID newId = UUID.randomUUID();

        when(mockRepository.findByTransactionIdAndTransactionDate(newId, this.transactionDate, this.pageable)).thenReturn(new PageImpl<>(new ArrayList<>()));

        Throwable noResults = assertThrows(ObjectNotFoundException.class, () -> this.testService.getConversionHistory(newId, this.transactionDate, this.pageable),
                "Should throw ObjectNotFoundException if no results are found by required criteria.");

        assertEquals("No data matching the provided criteria.",
                noResults.getMessage());
    }
}
