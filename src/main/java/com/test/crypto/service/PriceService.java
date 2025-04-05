package com.test.crypto.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
public class PriceService {

    public String getBestEthereum() throws URISyntaxException, IOException, InterruptedException {

        String symbolToQuery = "ETHUSDT";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Get prices from Binance API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.binance.com/api/v3/ticker/bookTicker?symbol=" + symbolToQuery))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (200 == response.statusCode()) {
            return response.body();
        } else {
            log.error("Failed to fetch data from Binance API. Status code: {}", response.statusCode());
            return "Failed to fetch data from Binance API";
        }
    }
}
