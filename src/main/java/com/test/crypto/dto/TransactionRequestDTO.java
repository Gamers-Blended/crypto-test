package com.test.crypto.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDTO {

    private String crypto;
    private BigDecimal amountInUsdt;
}
