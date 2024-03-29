package com.fea.foreign.exchange.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

@Service
public class ExchangeRateService {
    private final static String CURRENCY_LAYER_BASE_URL = "http://api.currencylayer.com/";
    private final static String LIVE_ENDPOINT = "live";
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
        URL url = httpConnection.createURL(CURRENCY_LAYER_BASE_URL + LIVE_ENDPOINT + currencyLayerAccessKey + SOURCE_PARAM + source + CURRENCIES_PARAM + target);
        InputStream stream = httpConnection.establishConnection(url);

        String response = httpConnection.getResponseFromStream(stream);

        return parseFxRate(response, source, target);
    }

    public BigDecimal parseFxRate(String response, String source, String target) {
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return jsonObject.getAsJsonObject("quotes").get("" + source + target).getAsBigDecimal();
    }
}
