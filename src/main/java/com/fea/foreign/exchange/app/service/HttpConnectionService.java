package com.fea.foreign.exchange.app.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class HttpConnectionService {
    public static InputStream establishConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        return conn.getInputStream();
    }

    public static URL createURL(String query) throws MalformedURLException {
        return new URL(query);
    }

    public static void closeStream(InputStream stream) throws IOException {
        stream.close();
    }
}
