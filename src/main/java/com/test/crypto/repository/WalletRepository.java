package com.test.crypto.repository;

import com.test.crypto.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    @Query(value = "SELECT * FROM crypto.wallet_balance_history ORDER BY id DESC LIMIT 1", nativeQuery = true)
    List<Wallet> getLatestWalletBalance();
}
