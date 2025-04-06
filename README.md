# crypto-test
A simple demo of a crypto trading system in Java

# Assumptions

- User has already authenticated and is authorised to access APIs
- User is only able to buy/sell ETHUSDT & BTCUSDT
- User's initial wallet balance is 50,000 USDT in database record
- System only supports a single user
- Wallet only holds 3 currencies: USDT, EHT and BTC
- Orders are executed at the latest ask/bid price
- No slippage and transaction fees

# API Endpoints
1 - `/prices` [GET] <br>
This is an automated endpoint that is triggered every 10 seconds after the previous one has finished. <br>
It is first triggered 10 seconds after application startup. <br>
Then triggered again 10 seconds after the first endpoint has finished its process. <br>
This endpoint retrieves prices of ETHUSDT and BTCUSDT from the Binance and Houbi APIs. <br>
It will save the lower ask price (and higher bid price) between the two sources into the `prices` database. <br>


2 - `/prices-manual` [GET] <br>
This endpoint retrieves the latest best aggregated price between the Binance and Houbi APIs on demand. <br>
No database calls.


3 - `/trade/buy"` [POST] <br>
This endpoint allows users to buy ETH or BTC with USDT. <br>
It requires 2 inputs in its request body:
- `crypto` - the symbol of the crypto to buy
- `amountInUsdt` - the amount of USDT used to buy crypto


4 - `/trade/sell"` [POST] <br>
This endpoint allows users to sell ETH or BTC and receive USDT. <br>
It requires 2 inputs in its request body:
- `crypto` - the symbol of the crypto to buy
- `amountToSell` - the amount of crypto to sell


5 - `/wallet` [GET] <br>
This endpoint retrieves the latest wallet balance of the user. <br>


6 - `/history` [GET] <br>
This endpoint retrieves the entire trading history of the user. <br>


# Demo
During the scheduled task, retrieved prices are logged (API #1).
![logs](docs/logs1.jpg)

Both Ask and Bid prices are been saved during the scheduled task (API #1).<br>
Do note that the prices are not saved every 10 seconds. <br>
This is because the price retrieval is only done 10 seconds after the previous job has finished. 
![prices database](docs/logs2.jpg)

Initial wallet balance has an initial top up of 50,000 USDT.
![initial wallet balance](docs/logs3.jpg)

Initial transaction audit table.
![initial wallet balance](docs/logs4.jpg)

This the output for getting the best aggregated price (API #2).
![get best price](docs/logs5.jpg)

This the output for getting the initial wallet balance (API #5).
![get initial wallet](docs/logs6.jpg)

This the output for getting the initial trading history (API #6).
![get initial history](docs/logs7.jpg)

Buy ETH with 5000 USDT (API #3)
![buy eth](docs/logs8.jpg)

Buy BTC with 5000 USDT (API #3)
![buy btc](docs/logs9.jpg)

Sell 2 ETH (API #4)
![sell eth](docs/logs10.jpg)

Sell 0.05 BTC (API #4)
![sell btc](docs/logs11.jpg)

Get wallet balance (API #5).
![get wallet](docs/logs12.jpg)

Get trading history (API #6). <br>
A total of 4 + 1 (initial topup) transactions.
![get history 1](docs/logs13.jpg)
![get history 2](docs/logs14.jpg)
![get history 3](docs/logs15.jpg)

Database for transactions audit table
![transaction table](docs/logs16.jpg)

Database for wallet balance
![wallet table](docs/logs17.jpg)

# Further Improvements
- Return a 400 error code for failed validation checks
- Add support for multiple users
- Add filter options for retrieving trading history