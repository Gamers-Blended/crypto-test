package com.test.crypto.controller;

import com.test.crypto.service.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("crypto/api/v1")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @Scheduled(fixedDelay = 10000) // 10 seconds in milliseconds
    @GetMapping("/ethereum")
    public void getEthereumPrices() throws URISyntaxException, IOException, InterruptedException {
        log.info("-- Start Ethereum price retrieval --");

//        String result = priceService.getBestEthereum();
        String result = priceService.getBestEthereumPrices();
        log.info(result);

        log.info("-- End Ethereum price retrieval --");
    }
}
