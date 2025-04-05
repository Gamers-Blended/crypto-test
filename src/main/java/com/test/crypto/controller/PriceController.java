package com.test.crypto.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("crypto/api/v1")
public class PriceController {

    @Scheduled(fixedDelay = 10000) // 10 seconds in milliseconds
    @GetMapping("/ethereum")
    public String getEthereumPrices() {
        log.info("-- Start Ethereum price retrieval --");

        log.info("-- End Ethereum price retrieval --");

        return "finished";
    }
}
