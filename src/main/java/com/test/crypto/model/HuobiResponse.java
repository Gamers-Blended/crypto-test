package com.test.crypto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HuobiResponse {

    private String status;
    private Long ts;
    private HuobiTickerData[] data;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HuobiTickerData {
        private String symbol;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Double amount;
        private Double vol;
        private Integer count;
        private BigDecimal bid;
        private Double bidSize;
        private BigDecimal ask;
        private Double askSize;
    }
}
