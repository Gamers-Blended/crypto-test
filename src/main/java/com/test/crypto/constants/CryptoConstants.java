package com.test.crypto.constants;

public class CryptoConstants {

    public static final String ETHUSDT = "ETHUSDT";
    public static final String BTCUSDT = "BTCUSDT";
    public static final String ETH = "ETH";
    public static final String BTC = "BTC";
    public static final String BUY = "BUY";
    public static final String SELL = "SELL";

    private CryptoConstants() {
        // utility classes should not be instantiated
        throw new IllegalStateException("Utility class");
    }
}
