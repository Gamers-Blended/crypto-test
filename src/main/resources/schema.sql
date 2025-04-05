-- Create schema
CREATE SCHEMA IF NOT EXISTS crypto;

-- Create table to track user's wallet balance
CREATE TABLE IF NOT EXISTS crypto.wallet_balance_history (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    usdt_amount DECIMAL NOT NULL,
    eth_amount DECIMAL NOT NULL,
    btc_amount DECIMAL NOT NULL,
    transaction_id INTEGER NOT NULL);

-- Create table to track user's trading history
CREATE TABLE IF NOT EXISTS crypto.transactions (
    transaction_id INTEGER PRIMARY KEY,
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
    bid_price DECIMAL NOT NULL,
    ask_price VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL);