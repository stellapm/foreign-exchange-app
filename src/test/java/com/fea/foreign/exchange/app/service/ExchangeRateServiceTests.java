package com.fea.foreign.exchange.app.service;

import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTests {
    @Mock
    private HttpConnectionService mockHttpConnectionService;
    @InjectMocks
    private ExchangeRateService testService;

    @BeforeEach
    void setUp() {
        testService.currencyLayerAccessKey = "?access_key=TestKey";
    }

    @Test
    public void testFetchFxRateByValidSourceAndTarget() throws IOException {
        String source = "USD";
        String target = "EUR";

        URL mockURL = mock(URL.class);
        InputStream mockStream = new ByteArrayInputStream("test data".getBytes());
        String mockResponse = "{\"quotes\":{\"USDEUR\":1.2345}}";

        when(mockHttpConnectionService.createURL(anyString())).thenReturn(mockURL);
        when(mockHttpConnectionService.establishConnection(any(URL.class))).thenReturn(mockStream);
        when(mockHttpConnectionService.getResponseFromStream(any(InputStream.class))).thenReturn(mockResponse);

        BigDecimal result = testService.fetchFxRateBySourceAndTarget(source, target);

        assertEquals(new BigDecimal("1.2345"), result);

        verify(mockHttpConnectionService, times(1)).establishConnection(any(URL.class));
        verify(mockHttpConnectionService, times(1)).getResponseFromStream(any(InputStream.class));
    }

    @Test
    public void testFetchFxRateByInvalidSourceAndTargetThrowsException() throws IOException {
        URL mockURL = mock(URL.class);
        when(mockHttpConnectionService.createURL(anyString())).thenReturn(mockURL);
        when(mockHttpConnectionService.establishConnection(any(URL.class))).thenThrow(new IOException("Failed to fetch result."));

        Throwable invalidRequest = assertThrows(IOException.class, () -> testService.fetchFxRateBySourceAndTarget("INVALID", "INVALID"));
        assertEquals("Failed to fetch result.", invalidRequest.getMessage());
    }
}
