package com.test.crypto.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BinanceResponse {
    private String symbol;
    private BigDecimal bidPrice;
    private Double bidQty;
    private BigDecimal askPrice;
    private Double askQty;
}
