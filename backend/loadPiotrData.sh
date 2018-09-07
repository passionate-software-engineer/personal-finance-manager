#!/usr/bin/env bash -e

# Script works correctly only on clean database - will fail if any entry already exists (e.g. account name)

# Register user
curl --request POST --url http://localhost:8088/users/register --header 'Authorization: Basic cGtvbGFjejpQaW9EZXYxMDEx' --header 'Cache-Control: no-cache' --header 'Content-Type: application/json' --header 'Postman-Token: b331a4d8-3a61-4192-ba4a-e3b3289342a6' --data '{"firstName":"Piotr","lastName":"Kołacz","username":"piotr","password":"piotr123"}'

# Add accounts
ACCOUNT_ALIOR_PLN=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 0, \"name\": \"kantor Alior - PLN\"}")
echo "$ACCOUNT_ALIOR_PLN"

ACCOUNT_ALIOR_USD=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 85980, \"name\": \"kantor Alior - USD\"}")
echo "$ACCOUNT_ALIOR_USD"

ACCOUNT_ALIOR_EUR=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 2617.22, \"name\": \"kantor Alior - EUR\"}")
echo "$ACCOUNT_ALIOR_EUR"

ACCOUNT_ALIOR_GBP=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 0, \"name\": \"kantor Alior - GBP\"}")
echo "$ACCOUNT_ALIOR_GBP"

ACCOUNT_IDEA=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 14374.23, \"name\": \"Idea Bank\"}")
echo "$ACCOUNT_IDEA"

ACCOUNT_IKE=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 12789.00, \"name\": \"IKE\"}")
echo "$ACCOUNT_IKE"

ACCOUNT_IKZE=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 5115.60, \"name\": \"IKZE\"}")
echo "$ACCOUNT_IKZE"

ACCOUNT_CASH=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 193.42, \"name\": \"Gotówka\"}")
echo "$ACCOUNT_CASH"

ACCOUNT_CASH_EUR=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 84.43, \"name\": \"Gotówka - euro\"}")
echo "$ACCOUNT_CASH_EUR"

ACCOUNT_ANIA_BALANCE=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": -6411.46, \"name\": \"Ania - rozliczenie\"}")
echo "$ACCOUNT_ANIA_BALANCE"

# Add categories
CATEGORY_INCOME=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Przychody\"}")
echo "$CATEGORY_INCOME"

CATEGORY_MONTH_SUMMARY=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Rozliczenie miesiąca\"}")
echo "$CATEGORY_MONTH_SUMMARY"

CATEGORY_FOOD=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Jedzenie\"}")
echo "$CATEGORY_FOOD"

CATEGORY_DIVING=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Nurkowanie\"}")
echo "$CATEGORY_DIVING"

CATEGORY_TRANSPORT=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Transport\"}")
echo "$CATEGORY_TRANSPORT"

CATEGORY_CURRENCY_EXCHANGE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Wymiana walut\"}")
echo "$CATEGORY_CURRENCY_EXCHANGE"

CATEGORY_ACCOMMODATION=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Wyjazdy - noclegi\"}")
echo "$CATEGORY_ACCOMMODATION"

CATEGORY_ENTERTAINMENT=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Rozrywka\"}")
echo "$CATEGORY_ENTERTAINMENT"

CATEGORY_PHONE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Telefon\"}")
echo "$CATEGORY_PHONE"

CATEGORY_SHOPPING=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Zakupy\"}")
echo "$CATEGORY_SHOPPING"

CATEGORY_RENOVATION=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Remont domu\"}")
echo "$CATEGORY_RENOVATION"

CATEGORY_INCOME_CROSSOVER=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Crossover\", \"parentCategoryId\": $CATEGORY_INCOME}")
echo "$CATEGORY_INCOME_CROSSOVER"

CATEGORY_INCOME_OTHER=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Inne Przychody\", \"parentCategoryId\": $CATEGORY_INCOME}")
echo "$CATEGORY_INCOME_OTHER"

CATEGORY_COMPANY_COSTS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Koszty prowadzenia firmy\"}")
echo "$CATEGORY_COMPANY_COSTS"

CATEGORY_COMPANY_ZUS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"ZUS\", \"parentCategoryId\": $CATEGORY_COMPANY_COSTS}")
echo "$CATEGORY_COMPANY_ZUS"

CATEGORY_COMPANY_ACCOUNTANT=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Księgowość\", \"parentCategoryId\": $CATEGORY_COMPANY_COSTS}")
echo "$CATEGORY_COMPANY_ACCOUNTANT"

CATEGORY_COMPANY_INCOME_TAX=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Podatek dochodowy\", \"parentCategoryId\": $CATEGORY_COMPANY_COSTS}")
echo "$CATEGORY_COMPANY_INCOME_TAX"

CATEGORY_COMPANY_VAT=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"VAT\", \"parentCategoryId\": $CATEGORY_COMPANY_COSTS}")
echo "$CATEGORY_COMPANY_VAT"

CATEGORY_COMPANY_LEASING=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Leasing samochodu\", \"parentCategoryId\": $CATEGORY_COMPANY_COSTS}")
echo "$CATEGORY_COMPANY_LEASING"

CATEGORY_CAR=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Samochód\"}")
echo "$CATEGORY_CAR"

CATEGORY_CAR_FUEL=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Paliwo\", \"parentCategoryId\": $CATEGORY_CAR}")
echo "$CATEGORY_CAR_FUEL"

# Transactions - January
TRANSACTION_CROSSOVER=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-04\", \"description\": \"Crossover 18-24.12\", \"price\": 7165.00}")
echo "$TRANSACTION_CROSSOVER"

TRANSACTION_IDEA_BONUS=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_OTHER, \"date\": \"2018-01-04\", \"description\": \"Idea Bank Premia\", \"price\": 50.00}")
echo "$TRANSACTION_IDEA_BONUS"

TRANSACTION_ZUS=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-01-05\", \"description\": \"ZUS - Styczeń\", \"price\": -473.20}")
echo "$TRANSACTION_ZUS"

TRANSACTION_INCOME_TAX=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_INCOME_TAX, \"date\": \"2018-01-15\", \"description\": \"Podatek dochodowy Q4 2017\", \"price\": -5746.00}")
echo "$TRANSACTION_INCOME_TAX"

TRANSACTION_VAT=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_VAT, \"date\": \"2018-01-15\", \"description\": \"VAT Q4 2017\", \"price\": -759}")
echo "$TRANSACTION_VAT"

TRANSACTION_CROSSOVER=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-18\", \"description\": \"Crossover Interviews - Grudzień\", \"price\": 985.19}")
echo "$TRANSACTION_CROSSOVER"

TRANSACTION_EXCHANGE=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-03\", \"description\": \"USD -> EUR\", \"price\": -3582.50}")
echo "$TRANSACTION_EXCHANGE"

TRANSACTION_EXCHANGE=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-03\", \"description\": \"USD -> EUR\", \"price\": 3513.56}")
echo "$TRANSACTION_EXCHANGE"

TRANSACTION_EXCHANGE=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-03\", \"description\": \"USD -> EUR - Ania zwrot\", \"price\": 34.47}")
echo "$TRANSACTION_EXCHANGE"

TRANSACTION_ACCOMMODATION=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-06\", \"description\": \"Noclegi Gran Canaria\", \"price\": -4418.60}")
echo "$TRANSACTION_ACCOMMODATION"

TRANSACTION_ACCOMMODATION=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-06\", \"description\": \"Noclegi Gran Canaria - Ania zwrot\", \"price\": 2209.30}")
echo "$TRANSACTION_ACCOMMODATION"

TRANSACTION_DIVING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-01-07\", \"description\": \"Nurkowanie - Top Diving Gran Canaria\", \"price\": -542.72}")
echo "$TRANSACTION_DIVING"

TRANSACTION_FOOD=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_FOOD, \"date\": \"2018-01-06\", \"description\": \"Jedzenie\", \"price\": -195.04}")
echo "$TRANSACTION_FOOD"

TRANSACTION_FOOD=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_FOOD, \"date\": \"2018-01-06\", \"description\": \"Jedzenie - Ania zwrot\", \"price\": 97.52}")
echo "$TRANSACTION_FOOD"

TRANSACTION_AUDIOTEKA=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_FOOD, \"date\": \"2018-01-08\", \"description\": \"Audioteka\", \"price\": -19.90}")
echo "$TRANSACTION_AUDIOTEKA"

TRANSACTION_ACCOMMODATION=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-10\", \"description\": \"Noclegi Fuerteventura - zaliczka\", \"price\": 979.23}")
echo "$TRANSACTION_ACCOMMODATION"

TRANSACTION_FERRY=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSPORT, \"date\": \"2018-01-10\", \"description\": \"Prom Fuerteventura\", \"price\": -565.28}")
echo "$TRANSACTION_FERRY"

TRANSACTION_FERRY=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-06\", \"description\": \"Prom Fuerteventura\", \"price\": 282.64}")
echo "$TRANSACTION_FERRY"

TRANSACTION_DIVING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-01-14\", \"description\": \"Nurkowanie - Las Palmas Gran Canaria\", \"price\": -468.31}")
echo "$TRANSACTION_DIVING"

TRANSACTION_EXCHANGE=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-13\", \"description\": \"USD -> EUR\", \"price\": -985.18}")
echo "$TRANSACTION_EXCHANGE"

TRANSACTION_EXCHANGE=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-13\", \"description\": \"USD -> EUR\", \"price\": 950.14}")
echo "$TRANSACTION_EXCHANGE"

TRANSACTION_ACCOUNTANT=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-01-09\", \"description\": \"Księgowy - Styczeń\", \"price\": -123.00}")
echo "$TRANSACTION_ACCOUNTANT"

TRANSACTION_LEASING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-01-26\", \"description\": \"Leasing - styczeń\", \"price\": -2308.97}")
echo "$TRANSACTION_LEASING"

TRANSACTION_LEASING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-01-26\", \"description\": \"Leasing - styczeń - Ania zwrot\", \"price\": 1154.49}")
echo "$TRANSACTION_LEASING"

TRANSACTION_PHONE=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-01-18\", \"description\": \"Telefon\", \"price\": -21.34}")
echo "$TRANSACTION_PHONE"

TRANSACTION_CAR_FUEL=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-01-26\", \"description\": \"Paliwo\", \"price\": -239.50}")
echo "$TRANSACTION_CAR_FUEL"

TRANSACTION_CAR_FUEL=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-01-26\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 119.75}")
echo "$TRANSACTION_CAR_FUEL"

TRANSACTION_SHOPPING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-01-26\", \"description\": \"Zakupy\", \"price\": -150}")
echo "$TRANSACTION_SHOPPING"

TRANSACTION_SHOPPING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-01-26\", \"description\": \"Zakupy - Ania zwrot\", \"price\": 75}")
echo "$TRANSACTION_SHOPPING"

TRANSACTION_CROSSOVER=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-18\", \"description\": \"Crossover 01-07.01\", \"price\": 7165.00}")
echo "$TRANSACTION_CROSSOVER"

TRANSACTION_FOOD=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_FOOD, \"date\": \"2018-01-06\", \"description\": \"Jedzenie\", \"price\": -135.68}")
echo "$TRANSACTION_FOOD"

TRANSACTION_FOOD=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_FOOD, \"date\": \"2018-01-06\", \"description\": \"Jedzenie - Ania zwrot\", \"price\": 67.84}")
echo "$TRANSACTION_FOOD"

TRANSACTION_ACCOMMODATION=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-10\", \"description\": \"Noclegi Fuerteventura \", \"price\": -979.23}")
echo "$TRANSACTION_ACCOMMODATION"

TRANSACTION_DIVING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-01-21\", \"description\": \"Nurkowanie - North Gran Canaria\", \"price\": -148.40}")
echo "$TRANSACTION_DIVING"

TRANSACTION_CROSSOVER=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-25\", \"description\": \"Crossover 08-14.01\", \"price\": 7165.00}")
echo "$TRANSACTION_CROSSOVER"

# TODO split into categories
TRANSACTION_MONTH=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_MONTH_SUMMARY, \"date\": \"2018-01-06\", \"description\": \"Jedzenie - Ania zwrot\", \"price\": -5865.25}")
echo "$TRANSACTION_MONTH"
