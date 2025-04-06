package com.test.crypto.service;

import com.test.crypto.dto.TransactionRequestDTO;
import com.test.crypto.model.Prices;
import com.test.crypto.repository.PricesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private PricesRepository pricesRepository;

    @Autowired
    private WalletService walletService;

    public BigDecimal buyCrypto(TransactionRequestDTO transactionRequestDTO) {
        // get latest ask price for chosen crypto
        log.info("Buying {} with {} USDT...", transactionRequestDTO.getCrypto(), transactionRequestDTO.getAmountInUsdt());
        List<Prices> latestPricesList = pricesRepository.getLatestPricesForSymbol(transactionRequestDTO.getCrypto());
        BigDecimal latestAskPrice = latestPricesList.get(0).getAskPrice();

        // check if enough balance after transaction

        return latestAskPrice;
    }
}
