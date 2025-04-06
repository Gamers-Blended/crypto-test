package com.test.crypto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", schema = "crypto")
@Getter
@Setter
public class Transaction {

    @Id
    private String transactionId;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String cryptoTraded;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal cryptoAmountTraded;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal usdtTraded;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal exchangeRate;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
