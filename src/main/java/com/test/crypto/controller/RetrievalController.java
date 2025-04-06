package com.test.crypto.controller;

import com.test.crypto.dto.WalletDTO;
import com.test.crypto.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("crypto/api/v1")
public class RetrievalController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/wallet")
    public WalletDTO getWalletBalance() {
        return walletService.getCurrentBalance();
    }
}
