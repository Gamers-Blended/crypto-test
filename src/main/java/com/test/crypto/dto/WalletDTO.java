package com.test.crypto.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletDTO {

    private BigDecimal usdt;
    private BigDecimal eth;
    private BigDecimal btc;
}
