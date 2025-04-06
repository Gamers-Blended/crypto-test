package com.test.crypto.service;

import com.test.crypto.dto.WalletDTO;
import com.test.crypto.model.Wallet;
import com.test.crypto.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public WalletDTO getCurrentBalance() {
        Wallet walletFromDb = walletRepository.getLatestWalletBalance().get(0);
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setUsdt(walletFromDb.getUsdtAmount());
        walletDTO.setEth(walletFromDb.getEthAmount());
        walletDTO.setBtc(walletFromDb.getBtcAmount());

        return walletDTO;
    }
}
