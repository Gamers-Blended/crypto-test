package com.test.crypto.repository;

import com.test.crypto.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query(value = "SELECT * FROM crypto.transactions ORDER BY created_at DESC", nativeQuery = true)
    List<Transaction> getTradingHistory();
}
