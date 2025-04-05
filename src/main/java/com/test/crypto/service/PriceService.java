package com.test.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.crypto.model.BinanceResponse;
import com.test.crypto.model.HuobiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PriceService {

    public String getBestEthereumPrices() throws URISyntaxException, IOException, InterruptedException {
        // askBidList is a list with 2 elements: askPrice, bidPrice
        // buy - lower ask price
        List<Double> askBidList = new ArrayList<>();
        String symbolToQuery = "ETHUSDT";

        List<Double> askBidFromBinanceList = getEthereumPricesFromBinance(symbolToQuery);
        List<Double> askBidFromHoubiList = getEthereumPricesFromHuobi(symbolToQuery);

        return askBidFromHoubiList.toString();
    }

    // returns list [askPrice, bidPrice]
    public List<Double> getEthereumPricesFromBinance(String symbolToQuery) throws URISyntaxException, IOException, InterruptedException {

        List<Double> askBidList = new ArrayList<>();

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
            ObjectMapper mapper = new ObjectMapper();
            BinanceResponse binanceResponse = mapper.readValue(response.body(), BinanceResponse.class);

            log.info("Retrieved prices from Binance: Ask: {}, Bid: {}", binanceResponse.getAskPrice(), binanceResponse.getBidPrice());
            askBidList.add(binanceResponse.getAskPrice());
            askBidList.add(binanceResponse.getBidPrice());
        } else {
            log.error("Failed to fetch data from Binance API. Status code: {}", response.statusCode());
        }
        return askBidList;
    }

    public List<Double> getEthereumPricesFromHuobi(String symbolToQuery) throws URISyntaxException, IOException, InterruptedException {

        List<Double> askBidList = new ArrayList<>();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Get prices from Huobi API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.huobi.pro/market/tickers"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (200 == response.statusCode()) {
            ObjectMapper mapper = new ObjectMapper();
            HuobiResponse huobiResponse = mapper.readValue(response.body(), HuobiResponse.class);

            String targetSymbolLower = symbolToQuery.toLowerCase();

            for (HuobiResponse.HuobiTickerData ticker : huobiResponse.getData()) {
                if (targetSymbolLower.equals(ticker.getSymbol().toLowerCase())) {

                    log.info("Retrieved prices from Houbi: Ask: {}, Bid: {}", ticker.getAsk(), ticker.getBid());
                    askBidList.add(ticker.getAsk());
                    askBidList.add(ticker.getBid());
                }
            }
        } else {
            log.error("Failed to fetch data from Huobi API. Status code: {}", response.statusCode());
        }
        return askBidList;
    }
}
