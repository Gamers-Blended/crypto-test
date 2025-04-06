-- Create schema
CREATE SCHEMA IF NOT EXISTS crypto;

-- Create table to track user's wallet balance
CREATE TABLE IF NOT EXISTS crypto.wallet_balance_history (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    usdt_amount DECIMAL(10, 2) NOT NULL,
    eth_amount DECIMAL(10, 2) NOT NULL,
    btc_amount DECIMAL(10, 2) NOT NULL,
    transaction_id VARCHAR NOT NULL);

-- Create table to track user's trading history
CREATE TABLE IF NOT EXISTS crypto.transactions (
    transaction_id VARCHAR PRIMARY KEY,
    user_id INTEGER NOT NULL,
    symbol VARCHAR NOT NULL,
    side VARCHAR(4) NOT NULL,
    price DECIMAL NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL);

-- Create table to save best aggregated prices
CREATE TABLE IF NOT EXISTS crypto.prices (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR NOT NULL,
    bid_price DECIMAL(10, 2) NOT NULL,
    ask_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL);