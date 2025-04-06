INSERT INTO crypto.wallet_balance_history (user_id, usdt_amount, eth_amount, btc_amount, transaction_id)
VALUES (1, 50000, 0, 0, 0);

INSERT INTO crypto.transactions (transaction_id, transaction_type, user_id, crypto_traded, crypto_amount_traded, usdt_traded, exchange_rate, created_at)
VALUES (0, 'TOP UP', 1, 'USDT', 50000, 50000, 1, CURRENT_TIMESTAMP());