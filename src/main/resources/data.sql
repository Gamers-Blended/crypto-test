INSERT INTO crypto.wallet_balance_history (user_id, usdt_amount, eth_amount, btc_amount, transaction_id)
VALUES (1, 50000, 0, 0, 0);

INSERT INTO crypto.transactions (transaction_id, user_id, symbol, side, price, quantity, created_at)
VALUES (0, 1, 'USDT', 'buy', 1, 50000, CURRENT_TIMESTAMP());