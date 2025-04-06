package com.test.crypto.controller;

import com.test.crypto.dto.TransactionRequestDTO;
import com.test.crypto.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.test.crypto.constants.CryptoConstants.BUY;
import static com.test.crypto.constants.CryptoConstants.SELL;

@Slf4j
@RestController
@RequestMapping("crypto/api/v1")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/trade/buy")
    public String buyCrypto(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.performTrade(transactionRequestDTO, BUY);
    }

    @PostMapping("/trade/sell")
    public String sellCrypto(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.performTrade(transactionRequestDTO, SELL);
    }
}
