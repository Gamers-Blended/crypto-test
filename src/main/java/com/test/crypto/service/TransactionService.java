package com.test.crypto.service;

import com.test.crypto.dto.TransactionRequestDTO;
import com.test.crypto.dto.WalletDTO;
import com.test.crypto.model.Prices;
import com.test.crypto.model.Transaction;
import com.test.crypto.model.Wallet;
import com.test.crypto.repository.PricesRepository;
import com.test.crypto.repository.TransactionRepository;
import com.test.crypto.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static com.test.crypto.constants.CryptoConstants.*;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private PricesRepository pricesRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    private final List<String> ALLOWED_CRYPTO_LIST = List.of(ETH, BTC);

    public String performTrade(TransactionRequestDTO transactionRequestDTO, String mode) {

        // check chosen crypto is allowed
        if (!ALLOWED_CRYPTO_LIST.contains(transactionRequestDTO.getCrypto())) {
            return "Unsupported cryptocurrency chosen, please only choose from this list: " + ALLOWED_CRYPTO_LIST;
        }

        String tranasctionId = UUID.randomUUID().toString();
        MathContext mc = new MathContext(18, RoundingMode.HALF_UP); // 18 precision, round half up
        switch (mode) {
            case BUY -> {
                return buyCrypto(transactionRequestDTO, tranasctionId, mc);
            }
            case SELL -> {
                return sellCrypto(transactionRequestDTO, tranasctionId, mc);
            }
            default -> {
                return "Invalid trade action, please only choose " + BUY + " or " + SELL;
            }
        }
    }

    public String buyCrypto(TransactionRequestDTO transactionRequestDTO, String tranasctionId, MathContext mc) {

        // check that buy amount is positive
        if (transactionRequestDTO.getAmountInUsdt().compareTo(BigDecimal.ZERO) < 0) {
            return "Amount to buy is invalid, please buy with an amount more than 0";
        }

        // check if enough balance after transaction
        WalletDTO wallet = walletService.getCurrentBalance();
        BigDecimal usdtBalance = wallet.getUsdt();
        BigDecimal usdtToTrade = transactionRequestDTO.getAmountInUsdt();
        if (usdtBalance.compareTo(usdtToTrade) < 0) {
            return "Insufficient funds to trade, current USDT amount: " + usdtBalance + ", trade amount: " + usdtToTrade;
        }

        // get the latest ask price for chosen crypto
        log.info("Buying {} with {} USDT...", transactionRequestDTO.getCrypto(), transactionRequestDTO.getAmountInUsdt());
        List<Prices> latestPricesList = pricesRepository.getLatestPricesForSymbol(cryptoConvertToPair(transactionRequestDTO.getCrypto()));
        if (latestPricesList.isEmpty()) {
            return "Unable to retrieve prices from database, please try again...";
        }

        // calculate amount to buy
        BigDecimal latestAskPrice = latestPricesList.get(0).getAskPrice();
        log.info("Buying {} at {}!", transactionRequestDTO.getCrypto(), latestAskPrice);

        BigDecimal amountToBuy = transactionRequestDTO.getAmountInUsdt().divide(latestAskPrice, mc);
        log.info("Amount of {} to add to wallet: {}", transactionRequestDTO.getCrypto(), amountToBuy);

        // update wallet
        // reduce USDT balance
        usdtBalance = usdtBalance.subtract(transactionRequestDTO.getAmountInUsdt(), mc);
        // increase bought crypto balance
        BigDecimal newBalance;
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId(1);
        updatedWallet.setUsdtAmount(usdtBalance);
        updatedWallet.setTransactionId(tranasctionId);
        switch (transactionRequestDTO.getCrypto()) {
            case ETH -> {
                newBalance = wallet.getEth().add(amountToBuy, mc);
                updatedWallet.setEthAmount(newBalance);
                updatedWallet.setBtcAmount(wallet.getBtc());
            }
            case BTC -> {
                newBalance = wallet.getBtc().add(amountToBuy, mc);
                updatedWallet.setBtcAmount(newBalance);
                updatedWallet.setEthAmount(wallet.getEth());
            }
            default -> {
                return "Unsupported cryptocurrency chosen, please only choose from this list: " + ALLOWED_CRYPTO_LIST;
            }
        }
        walletRepository.save(updatedWallet);

        // log transaction in transaction audit table
        Transaction transaction = new Transaction();
        transaction.setTransactionId(tranasctionId);
        transaction.setTransactionType("BUY");
        transaction.setUserId(1);
        transaction.setCryptoTraded(transactionRequestDTO.getCrypto());
        transaction.setCryptoAmountTraded(amountToBuy);
        transaction.setUsdtTraded(transactionRequestDTO.getAmountInUsdt());
        transaction.setExchangeRate(latestAskPrice);

        transactionRepository.save(transaction);

        return "Transaction completed successfully!" +
                "\n Transaction ID: " + tranasctionId +
                "\n Wallet balance: " +
                "\n USDT: " + updatedWallet.getUsdtAmount().toPlainString() +
                "\n ETH: " + updatedWallet.getEthAmount().toPlainString() +
                "\n BTC: " + updatedWallet.getBtcAmount().toPlainString();
    }

    public String sellCrypto(TransactionRequestDTO transactionRequestDTO, String tranasctionId, MathContext mc) {

        String cryptoToSell = transactionRequestDTO.getCrypto();
        // check that sell amount is positive
        if (transactionRequestDTO.getAmountToSell().compareTo(BigDecimal.ZERO) < 0) {
            return "Amount to sell is invalid, please sell at an amount more than 0";
        }

        // check if enough balance after transaction
        WalletDTO wallet = walletService.getCurrentBalance();
        BigDecimal cryptoToSellBalance = new BigDecimal(0);
        if (ETH.equalsIgnoreCase(cryptoToSell)) {
            cryptoToSellBalance = wallet.getEth();
        } else if (BTC.equalsIgnoreCase(cryptoToSell)) {
            cryptoToSellBalance = wallet.getBtc();
        }
        BigDecimal amountToSell = transactionRequestDTO.getAmountToSell();
        if (cryptoToSellBalance.compareTo(amountToSell) < 0) {
            return "Insufficient " + cryptoToSell + " to sell, current " + cryptoToSell + " amount: " + cryptoToSellBalance + ", sell amount: " + amountToSell;
        }

        // get the latest bid price for chosen crypto
        List<Prices> latestPricesList = pricesRepository.getLatestPricesForSymbol(cryptoConvertToPair(transactionRequestDTO.getCrypto()));
        if (latestPricesList.isEmpty()) {
            return "Unable to retrieve prices from database, please try again...";
        }

        // calculate amount of USDT to receive
        BigDecimal latestBidPrice = latestPricesList.get(0).getBidPrice();
        log.info("Selling {} {} at {}!", transactionRequestDTO.getAmountToSell(), transactionRequestDTO.getCrypto(), latestBidPrice);

        BigDecimal usdtAmountToReceive = transactionRequestDTO.getAmountToSell().multiply(latestBidPrice, mc);
        log.info("Amount of USDT to add to wallet: {}", usdtAmountToReceive);

        // update wallet
        // increase USDT balance
        BigDecimal newUsdTBalance = wallet.getUsdt().add(usdtAmountToReceive, mc);
        // decrease sold crypto balance
        BigDecimal newBalance;
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId(1);
        updatedWallet.setUsdtAmount(newUsdTBalance);
        updatedWallet.setTransactionId(tranasctionId);
        switch (transactionRequestDTO.getCrypto()) {
            case ETH -> {
                newBalance = wallet.getEth().subtract(amountToSell, mc);
                updatedWallet.setEthAmount(newBalance);
                updatedWallet.setBtcAmount(wallet.getBtc());
            }
            case BTC -> {
                newBalance = wallet.getBtc().subtract(amountToSell, mc);
                updatedWallet.setBtcAmount(newBalance);
                updatedWallet.setEthAmount(wallet.getEth());
            }
            default -> {
                return "Unsupported cryptocurrency chosen, please only choose from this list: " + ALLOWED_CRYPTO_LIST;
            }
        }
        walletRepository.save(updatedWallet);

        // log transaction in transaction audit table
        Transaction transaction = new Transaction();
        transaction.setTransactionId(tranasctionId);
        transaction.setTransactionType("SELL");
        transaction.setUserId(1);
        transaction.setCryptoTraded(transactionRequestDTO.getCrypto());
        transaction.setCryptoAmountTraded(amountToSell);
        transaction.setUsdtTraded(usdtAmountToReceive);
        transaction.setExchangeRate(latestBidPrice);

        transactionRepository.save(transaction);

        return "Transaction completed successfully!" +
                "\n Transaction ID: " + tranasctionId +
                "\n Wallet balance: " +
                "\n USDT: " + updatedWallet.getUsdtAmount().toPlainString() +
                "\n ETH: " + updatedWallet.getEthAmount().toPlainString() +
                "\n BTC: " + updatedWallet.getBtcAmount().toPlainString();
    }

    // for making queries to prices table
    private String cryptoConvertToPair(String cryptoSymbol) {
        if (ETH.equalsIgnoreCase(cryptoSymbol)) {
            return ETHUSDT;
        } else if (BTC.equalsIgnoreCase(cryptoSymbol)) {
            return  BTCUSDT;
        } else {
            return "";
        }
    }
}
