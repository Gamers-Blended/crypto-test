package com.test.crypto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet_balance_history", schema = "crypto")
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal usdtAmount;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal ethAmount;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal btcAmount;

    @Column(nullable = false)
    private String transactionId;

}
