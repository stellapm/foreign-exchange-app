package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.exceptions.IllegalParamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTests {
    private final static String VALID_SOURCE = "USD";
    private final static String VALID_TARGET = "BGN";
    private final static String INVALID_SOURCE_TARGET = "INVALID";

    @Mock
    private HttpConnectionService mockHttpConnectionService;
    @InjectMocks
    private ExchangeRateService testService;
    private HashSet<String> availableCurrencies;

    @BeforeEach
    public void setup(){
        this.availableCurrencies = new HashSet<>(List.of("USD", "EUR", "JPY", "BGN"));
    }

    @Test
    public void testFetchFxRateByInvalidSourceAndTarget() throws IOException {
        when(this.mockHttpConnectionService.requestCurrencies()).thenReturn(this.availableCurrencies);

        Throwable failedSourceValidation = assertThrows(IllegalParamException.class, () -> testService.fetchFxRateBySourceAndTarget(INVALID_SOURCE_TARGET, VALID_SOURCE),
                "Should throw IllegalParamException for invalid currency codes");

        assertEquals("Please make sure all requested parameters are provided: Currency", failedSourceValidation.getMessage());

        Throwable failedTargetValidation = assertThrows(IllegalParamException.class, () -> testService.fetchFxRateBySourceAndTarget(VALID_SOURCE, INVALID_SOURCE_TARGET),
                "Should throw IllegalParamException for invalid currency codes");

        assertEquals("Please make sure all requested parameters are provided: Currency", failedTargetValidation.getMessage());
    }

    @Test
    public void testFetchFxRateByValidSourceAndTarget() throws IOException {
        when(this.mockHttpConnectionService.requestCurrencies()).thenReturn(availableCurrencies);
        when(this.mockHttpConnectionService.requestFXRate(VALID_SOURCE, VALID_TARGET)).thenReturn(BigDecimal.valueOf(0.928915));

        BigDecimal result = this.testService.fetchFxRateBySourceAndTarget(VALID_SOURCE, VALID_TARGET);

        assertEquals(BigDecimal.valueOf(0.928915), result);
    }
}
