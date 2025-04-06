package com.test.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.crypto.model.BinanceResponse;
import com.test.crypto.model.HuobiResponse;
import com.test.crypto.model.Prices;
import com.test.crypto.repository.PricesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private PricesRepository pricesRepository;

    public String getBestEthereumPrices() throws URISyntaxException, IOException, InterruptedException {
        String symbolToQuery = "ETHUSDT";

        // askBidList is a list with 2 elements: askPrice, bidPrice
        List<BigDecimal> askBidFromBinanceList = getEthereumPricesFromBinance(symbolToQuery);
        List<BigDecimal> askBidFromHoubiList = getEthereumPricesFromHuobi(symbolToQuery);

        // get only the best prices from the 2 sources
        // buy - lower ask price
        // sell - higher bid price
        BigDecimal lowerAskPrice = askBidFromBinanceList.get(0).min(askBidFromHoubiList.get(0));
        BigDecimal higherBidPrice = askBidFromBinanceList.get(1).max(askBidFromHoubiList.get(1));

        // save best prices to database
        log.info("Saving prices for {}: Bid: {}, Ask: {}", symbolToQuery, higherBidPrice, lowerAskPrice);
        savePrices("ETHUSDT", lowerAskPrice, higherBidPrice);
        return "Ask: " + lowerAskPrice + ", Bid: " + higherBidPrice;
    }

    // returns list [askPrice, bidPrice]
    public List<BigDecimal> getEthereumPricesFromBinance(String symbolToQuery) throws URISyntaxException, IOException, InterruptedException {

        List<BigDecimal> askBidList = new ArrayList<>();

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

    public List<BigDecimal> getEthereumPricesFromHuobi(String symbolToQuery) throws URISyntaxException, IOException, InterruptedException {

        List<BigDecimal> askBidList = new ArrayList<>();

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

    private void savePrices(String symbol, BigDecimal askPrice, BigDecimal bidPrice) {
        Prices prices = new Prices();
        prices.setSymbol(symbol);
        prices.setAskPrice(askPrice.setScale(2, RoundingMode.HALF_UP));
        prices.setBidPrice(bidPrice.setScale(2, RoundingMode.HALF_UP));
        pricesRepository.save(prices);
    }
}
