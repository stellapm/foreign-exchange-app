package com.fea.foreign.exchange.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;

@Service
public class ExchangeRateService {
    private final static String CURRENCY_LAYER_BASE_URL = "http://api.currencylayer.com/";
    private final static String LIVE_ENDPOINT = "live";
    private final static String SOURCE_PARAM = "&source=";
    private final static String CURRENCIES_PARAM = "&currencies=";

    @Value("?access_key=" + "${app.currencyLayerAccessKey}")
    private String currencyLayerAccessKey;

    public BigDecimal fetchFxRateBySourceAndTarget(String source, String target) throws IOException {
        URL url = HttpConnectionService.createURL(CURRENCY_LAYER_BASE_URL + LIVE_ENDPOINT + currencyLayerAccessKey + SOURCE_PARAM + source + CURRENCIES_PARAM + target);
        InputStream stream = HttpConnectionService.establishConnection(url);

        String response = getResponseFromStream(stream);
        HttpConnectionService.closeStream(stream);

        return parseFxRate(response, source, target);
    }

    private BigDecimal parseFxRate(String response, String source, String target) {
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return jsonObject.getAsJsonObject("quotes").get("" + source + target).getAsBigDecimal();
    }

    private static String getResponseFromStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder response = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
}
