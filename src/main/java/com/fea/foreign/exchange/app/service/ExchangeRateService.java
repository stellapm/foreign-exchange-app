package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.exceptions.IllegalParamException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fea.foreign.exchange.app.constants.General.CURRENCY;

@Service
public class ExchangeRateService {
    private final static String CURRENCY_LAYER_BASE_URL = "http://api.currencylayer.com/";
    private final static String LIVE_ENDPOINT = "live";
    private final static String LIST_ENDPOINT = "list";
    private final static String SOURCE_PARAM = "&source=";
    private final static String CURRENCIES_PARAM = "&currencies=";

    @Value("?access_key=" + "${app.currencyLayerAccessKey}")
    public String currencyLayerAccessKey;

    private HttpConnectionService httpConnection;

    @Autowired
    public ExchangeRateService(HttpConnectionService httpConnection) {
        this.httpConnection = httpConnection;
    }

    //May hold methods concerning exchange rate operations: fetch by single currency target, multiple currency targets, historical exchange rates, etc.

    public BigDecimal fetchFxRateBySourceAndTarget(String source, String target) throws IOException {
        validateCurrency(source, target);

        URL url = httpConnection.createURL(CURRENCY_LAYER_BASE_URL + LIVE_ENDPOINT + currencyLayerAccessKey + SOURCE_PARAM + source + CURRENCIES_PARAM + target);
        InputStream stream = httpConnection.establishConnection(url);

        String response = httpConnection.getResponseFromStream(stream);

        return parseFxRate(response, source, target);
    }

    private void validateCurrency(String... args) throws IOException {
        HashSet<String> availableCurrencies = getCurrencies();

        for (String currency : args) {
            if (!availableCurrencies.contains(currency)){
                throw new IllegalParamException(CURRENCY);
            }
        }
    }

    private HashSet<String> getCurrencies() throws IOException {
        URL url = httpConnection.createURL(CURRENCY_LAYER_BASE_URL + LIST_ENDPOINT + currencyLayerAccessKey);
        InputStream stream = httpConnection.establishConnection(url);

        String response = httpConnection.getResponseFromStream(stream);

        return (HashSet<String>) parseCurrenciesList(response);
    }

    private Set<String> parseCurrenciesList(String response) {
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return new HashSet<>(jsonObject.getAsJsonObject("currencies").keySet());
    }

    private BigDecimal parseFxRate(String response, String source, String target) {
        System.out.println(response);
        System.out.println(source);
        System.out.println(target);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return jsonObject.getAsJsonObject("quotes").get("" + source + target).getAsBigDecimal();
    }
}
