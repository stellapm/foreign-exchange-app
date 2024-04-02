package com.fea.foreign.exchange.app.service;

import com.fea.foreign.exchange.app.controller.GlobalExceptionHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Service
public class HttpConnectionService {
    private final static String CURRENCY_LAYER_BASE_URL = "http://api.currencylayer.com/";
    private final static String LIVE_ENDPOINT = "live";
    private final static String LIST_ENDPOINT = "list";
    private final static String SOURCE_PARAM = "&source=";
    private final static String CURRENCIES_PARAM = "&currencies=";

    @Value("?access_key=" + "${app.currencyLayerAccessKey}")
    public String currencyLayerAccessKey;
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Cacheable("exchangeRates")
    public BigDecimal requestFXRate(String source, String target) throws IOException {
        URL url = createURL(CURRENCY_LAYER_BASE_URL + LIVE_ENDPOINT + currencyLayerAccessKey + SOURCE_PARAM + source + CURRENCIES_PARAM + target);
        InputStream stream = establishConnection(url);

        String response = getResponseFromStream(stream);
        return parseFxRate(response, source, target);
    }

    @CacheEvict(value = "exchangeRates", allEntries = true)
    @Scheduled(fixedRateString = "${app.exchangeRatesTTL}")
    public void emptyExchangeRateCache() {
        logger.info("Emptying exchange rates cache");
    }

    @Cacheable("currencies")
    public HashSet<String> requestCurrencies() throws IOException {
        logger.info("Fetching currencies from external service");

        URL url = createURL(CURRENCY_LAYER_BASE_URL + LIST_ENDPOINT + currencyLayerAccessKey);
        InputStream stream = establishConnection(url);

        String response = getResponseFromStream(stream);

        return (HashSet<String>) parseCurrenciesList(response);
    }

    private Set<String> parseCurrenciesList(String response) {
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return new HashSet<>(jsonObject.getAsJsonObject("currencies").keySet());
    }

    private BigDecimal parseFxRate(String response, String source, String target) {
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return jsonObject.getAsJsonObject("quotes").get("" + source + target).getAsBigDecimal();
    }

    private String getResponseFromStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder response = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    private InputStream establishConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        return conn.getInputStream();
    }

    private URL createURL(String query) throws MalformedURLException {
        return new URL(query);
    }
}
