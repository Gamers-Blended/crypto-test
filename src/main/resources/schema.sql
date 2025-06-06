-- Create schema
CREATE SCHEMA IF NOT EXISTS crypto;

-- Create table to track user's wallet balance
CREATE TABLE IF NOT EXISTS crypto.wallet_balance_history (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    usdt_amount DECIMAL(18, 8) NOT NULL,
    eth_amount DECIMAL(18, 8) NOT NULL,
    btc_amount DECIMAL(18, 8) NOT NULL,
    transaction_id VARCHAR NOT NULL);

-- Create table to track user's trading history
CREATE TABLE IF NOT EXISTS crypto.transactions (
    transaction_id VARCHAR PRIMARY KEY,
    transaction_type VARCHAR(6) NOT NULL,
    user_id INTEGER NOT NULL,
    crypto_traded VARCHAR NOT NULL,
    crypto_amount_traded DECIMAL(18, 8) NOT NULL,
    usdt_traded DECIMAL(18, 8) NOT NULL,
    exchange_rate DECIMAL(18, 8) NOT NULL,
    created_at TIMESTAMP NOT NULL);

-- Create table to save best aggregated prices
CREATE TABLE IF NOT EXISTS crypto.prices (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR NOT NULL,
    bid_price DECIMAL(18, 8) NOT NULL,
    ask_price DECIMAL(18, 8) NOT NULL,
    created_at TIMESTAMP NOT NULL);