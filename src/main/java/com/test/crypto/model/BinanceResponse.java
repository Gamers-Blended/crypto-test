package com.test.crypto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinanceResponse {
    private String symbol;
    private Double bidPrice;
    private Double bidQty;
    private Double askPrice;
    private Double askQty;
}
