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

    private final List<String> ALLOWED_CRYPTO_LIST = List.of("ETHUSDT", "BTCUSDT");

    public String buyCrypto(TransactionRequestDTO transactionRequestDTO) {

        // check chosen crypto is allowed
        if (!ALLOWED_CRYPTO_LIST.contains(transactionRequestDTO.getCrypto())) {
            return "Supported cryptocurrency chosen, please only choose from this list: " + ALLOWED_CRYPTO_LIST;
        }

        // check that trade amount is positive
        if (transactionRequestDTO.getAmountInUsdt().compareTo(BigDecimal.ZERO) < 0) {
            return "Amount to trade is invalid, please trade with an amount more than 0";
        }

        // check if enough balance after transaction
        BigDecimal usdtBalance = walletService.getCurrentBalance().getUsdt();
        BigDecimal usdtToTrade = transactionRequestDTO.getAmountInUsdt();
        if (usdtBalance.compareTo(usdtToTrade) < 0) {
            return "Insufficient funds to trade, current USDT amount: " + usdtBalance + ", trade amount: " + usdtToTrade;
        }

        // get latest ask price for chosen crypto
        log.info("Buying {} with {} USDT...", transactionRequestDTO.getCrypto(), transactionRequestDTO.getAmountInUsdt());
        List<Prices> latestPricesList = pricesRepository.getLatestPricesForSymbol(transactionRequestDTO.getCrypto());
        if (latestPricesList.isEmpty()) {
            return "Unable to retrieve prices from database, please try again...";
        }

        BigDecimal latestAskPrice = latestPricesList.get(0).getAskPrice();



        return "Transaction completed! Transaction ID: " + 1;
    }
}
