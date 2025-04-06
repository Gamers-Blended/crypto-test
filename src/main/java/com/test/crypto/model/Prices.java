package com.test.crypto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prices", schema = "crypto")
@Getter
@Setter
public class Prices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal bidPrice;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal askPrice;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
