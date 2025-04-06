package com.test.crypto.controller;

import com.test.crypto.dto.WalletDTO;
import com.test.crypto.model.Transaction;
import com.test.crypto.service.TransactionService;
import com.test.crypto.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("crypto/api/v1")
public class RetrievalController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/wallet")
    public WalletDTO getWalletBalance() {
        return walletService.getCurrentBalance();
    }

    @GetMapping("/history")
    public List<Transaction> getTradingHistory() {
        return transactionService.getTradingHistory();
    }
}
