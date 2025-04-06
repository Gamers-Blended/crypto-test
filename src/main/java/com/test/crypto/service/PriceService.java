package com.test.crypto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.crypto.dto.PriceDTO;
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

import static com.test.crypto.constants.CryptoConstants.BTCUSDT;
import static com.test.crypto.constants.CryptoConstants.ETHUSDT;

@Slf4j
@Service
public class PriceService {

    @Autowired
    private PricesRepository pricesRepository;

    public List<PriceDTO> getBestPrices(String mode) throws URISyntaxException, IOException, InterruptedException {

        List<PriceDTO> priceDTOList = new ArrayList<>();
        List<String> symbolsToQueryList = List.of(ETHUSDT, BTCUSDT);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        for (String symbolToQuery: symbolsToQueryList) {
            log.info("Processing {}", symbolToQuery);
            // askBidList is a list with 2 elements: askPrice, bidPrice
            List<BigDecimal> askBidFromBinanceList = getPricesFromBinance(symbolToQuery, client);
            List<BigDecimal> askBidFromHoubiList = getPricesFromHuobi(symbolToQuery, client);

            // get only the best prices from the 2 sources
            // buy - lower ask price
            // sell - higher bid price
            BigDecimal lowerAskPrice = askBidFromBinanceList.get(0).min(askBidFromHoubiList.get(0)).setScale(8, RoundingMode.HALF_UP);
            BigDecimal higherBidPrice = askBidFromBinanceList.get(1).max(askBidFromHoubiList.get(1)).setScale(8, RoundingMode.HALF_UP);

            // for scheduled API, save best prices to database
            if ("auto".equalsIgnoreCase(mode)) {
                log.info("Saving prices for {}: Bid: {}, Ask: {}", symbolToQuery, higherBidPrice, lowerAskPrice);
                savePrices(symbolToQuery, lowerAskPrice, higherBidPrice);
            }

            // for manual API, return result
            PriceDTO priceDTO = new PriceDTO();
            priceDTO.setSymbol(symbolToQuery);
            priceDTO.setAskPrice(lowerAskPrice);
            priceDTO.setBidPrice(higherBidPrice);
            priceDTOList.add(priceDTO);
        }

        return priceDTOList;
    }

    // returns list [askPrice, bidPrice]
    public List<BigDecimal> getPricesFromBinance(String symbolToQuery, HttpClient client) throws URISyntaxException, IOException, InterruptedException {

        List<BigDecimal> askBidList = new ArrayList<>();

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

    // returns list [askPrice, bidPrice]
    public List<BigDecimal> getPricesFromHuobi(String symbolToQuery, HttpClient client) throws URISyntaxException, IOException, InterruptedException {

        List<BigDecimal> askBidList = new ArrayList<>();

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
        prices.setAskPrice(askPrice);
        prices.setBidPrice(bidPrice);
        pricesRepository.save(prices);
    }
}
