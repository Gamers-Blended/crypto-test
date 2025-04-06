package com.test.crypto.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDTO {

    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;

}
