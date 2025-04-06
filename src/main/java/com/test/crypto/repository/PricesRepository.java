package com.test.crypto.repository;

import com.test.crypto.model.Prices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricesRepository extends JpaRepository<Prices, Integer> {

    @Query(value = "SELECT * FROM crypto.prices WHERE symbol = :symbol ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    List<Prices> getLatestPricesForSymbol(@Param("symbol") String symbol);

}
