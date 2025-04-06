package com.test.crypto.service;

import com.test.crypto.dto.TransactionRequestDTO;
import com.test.crypto.dto.WalletDTO;
import com.test.crypto.model.Prices;
import com.test.crypto.model.Wallet;
import com.test.crypto.repository.PricesRepository;
import com.test.crypto.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static com.test.crypto.constants.CryptoPairConstants.BTCUSDT;
import static com.test.crypto.constants.CryptoPairConstants.ETHUSDT;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private PricesRepository pricesRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;

    private final List<String> ALLOWED_CRYPTO_LIST = List.of(ETHUSDT, BTCUSDT);

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
        WalletDTO wallet = walletService.getCurrentBalance();
        BigDecimal usdtBalance = wallet.getUsdt();
        BigDecimal usdtToTrade = transactionRequestDTO.getAmountInUsdt();
        if (usdtBalance.compareTo(usdtToTrade) < 0) {
            return "Insufficient funds to trade, current USDT amount: " + usdtBalance + ", trade amount: " + usdtToTrade;
        }

        // get the latest ask price for chosen crypto
        log.info("Buying {} with {} USDT...", transactionRequestDTO.getCrypto(), transactionRequestDTO.getAmountInUsdt());
        List<Prices> latestPricesList = pricesRepository.getLatestPricesForSymbol(transactionRequestDTO.getCrypto());
        if (latestPricesList.isEmpty()) {
            return "Unable to retrieve prices from database, please try again...";
        }

        // calculate amount to buy
        BigDecimal latestAskPrice = latestPricesList.get(0).getAskPrice();
        log.info("Buying {} at {}!", transactionRequestDTO.getCrypto(), latestAskPrice);

        MathContext mc = new MathContext(18, RoundingMode.HALF_UP); // 18 precision, round half up
        BigDecimal amountToBuy = transactionRequestDTO.getAmountInUsdt().divide(latestAskPrice, mc);
        log.info("Amount of {} to add to wallet: {}", transactionRequestDTO.getCrypto(), amountToBuy);

        String tranasctionId = UUID.randomUUID().toString();
        // update wallet
        // reduce USDT balance
        usdtBalance = usdtBalance.subtract(transactionRequestDTO.getAmountInUsdt());
        // increase bought crypto balance
        BigDecimal newBalance;
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId(1);
        updatedWallet.setUsdtAmount(usdtBalance);
        updatedWallet.setTransactionId(tranasctionId);
        switch (transactionRequestDTO.getCrypto()) {
            case ETHUSDT:
                newBalance = wallet.getEth().add(amountToBuy);
                updatedWallet.setEthAmount(newBalance);
                updatedWallet.setBtcAmount(wallet.getBtc());
                break;
            case BTCUSDT:
                newBalance = wallet.getBtc().add(amountToBuy);
                updatedWallet.setBtcAmount(newBalance);
                updatedWallet.setEthAmount(wallet.getEth());
                break;
        }
        walletRepository.save(updatedWallet);

        return "Transaction completed! Transaction ID: " + tranasctionId +
                "\n Wallet balance: " +
                "\n USDT: " + updatedWallet.getUsdtAmount().toPlainString() +
                "\n ETH: " + updatedWallet.getEthAmount().toPlainString() +
                "\n BTC: " + updatedWallet.getBtcAmount().toPlainString();
    }
}
