package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionInfoView;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionResponseView;
import com.fea.foreign.exchange.app.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Could be generated either due to invalid parameters (non existing currency codes, blank strings, etc) " +
                "or failed validation on DTO (submitting non-positive BigDecimal, blank or null strings)",
                content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error due to IOException. " +
                "Could be generated as a result of failed established connection to external service provider or while processing the results from stream.",
                content = @Content)
})
public class CurrencyConversionController {
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public CurrencyConversionController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @PostMapping(CONVERT)
    @Operation(summary = "Submit request to convert currency", description = "Checks the exchange rate between two currencies and converts the amount. " +
            "Returns information about the transaction that includes a unique transaction ID and the converted amount in the target currency")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Currency conversion request details", required = true,
            content = @Content(schema = @Schema(implementation = CurrencyConversionRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "Successful currency conversion",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CurrencyConversionResponseView.class))})
    public ResponseEntity<CurrencyConversionResponseView> convertCurrency(@RequestBody @Valid CurrencyConversionRequestDTO currencyConversionRequestDTO) throws IOException {
        CurrencyConversionResponseView response = this.currencyConversionService.convertCurrency(currencyConversionRequestDTO);

        return ResponseEntity.ok(response);
    }


    @GetMapping(HISTORY)
    @Operation(summary = "Get paginated currency conversions by ID and/or date. Default size is " + DEFAULT_PAGE_SIZE,
            description = "Ensures at least one field is present and retrieves from database all entries that match the provided criteria.\n" +
                    "You can sort by: id, sourceCurrency, sourceAmount, targetCurrency, targetAmount, transactionID, transactionDate",
            responses = {
                    @ApiResponse(description = "Successfully retrieve history conversions", responseCode = "200",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "No result found that matches the criteria provided",
                            content = @Content)
            })
    public ResponseEntity<Page<CurrencyConversionInfoView>> getConversionHistory(
            @Parameter(description = "Transaction ID", example = "63b64ef0-2a58-4883-b889-12c16a1f2e3e")
            @RequestParam(required = false) UUID transactionID,
            @Parameter(description = "Transaction date", example = "2024-04-02")
            @RequestParam(required = false) LocalDate transactionDate,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable){
        Page<CurrencyConversionInfoView> conversionHistory = this.currencyConversionService.getConversionHistory(transactionID, transactionDate, pageable);

        return ResponseEntity.ok(conversionHistory);
    }
}
