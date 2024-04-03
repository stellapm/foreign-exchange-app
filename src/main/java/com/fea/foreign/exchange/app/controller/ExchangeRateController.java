package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

import static com.fea.foreign.exchange.app.constants.Endpoints.*;

@RestController
@RequestMapping(API + FX_RATE)
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid parameters: non existing currency codes, blank strings, etc.",
                content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error due to IOException. " +
                "Could be generated as a result of failed established connection to external service provider or while processing the results from stream.",
                content = @Content)
})
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping(GET)
    @Operation(summary = "Get Exchange Rate", description = "Retrieves the exchange rate between two currencies (source and target)",
            responses = {
                    @ApiResponse(description = "Successfully retrieve exchange rate", responseCode = "200",
                            content = @Content(mediaType = "application/json")),
            })
    public ResponseEntity<BigDecimal> getExchangeRate(
            @Parameter(description = "Source currency code", example = "USD")
            @RequestParam String sourceCurrency,
            @Parameter(description = "Target currency code", example = "EUR")
            @RequestParam String targetCurrency) throws IOException {
        BigDecimal exchangeRate = this.exchangeRateService.fetchFxRateBySourceAndTarget(sourceCurrency, targetCurrency);

        return ResponseEntity.ok(exchangeRate);
    }
}
