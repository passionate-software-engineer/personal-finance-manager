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

ACCOUNT_MONIKA_LOAN=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 0, \"name\": \"Monika - pożyczka\"}")
echo "$ACCOUNT_MONIKA_LOAN"

# Add categories
CATEGORY_INCOME=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Przychody\"}")
echo "$CATEGORY_INCOME"

CATEGORY_CHILD=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Dziecko\"}")
echo "$CATEGORY_CHILD"

CATEGORY_HOME_MAINTENANCE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Utrzymanie domu\"}")
echo "$CATEGORY_HOME_MAINTENANCE"

CATEGORY_BILLS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Rachunki\"}")
echo "$CATEGORY_BILLS"

CATEGORY_BILLS_TV=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"TV\", \"parentCategoryId\": $CATEGORY_BILLS}")
echo "$CATEGORY_BILLS_TV"

CATEGORY_BILLS_GAS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Gaz\", \"parentCategoryId\": $CATEGORY_BILLS}")
echo "$CATEGORY_BILLS_GAS"

CATEGORY_BILLS_INTERNET=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Internet\", \"parentCategoryId\": $CATEGORY_BILLS}")
echo "$CATEGORY_BILLS_INTERNET"

CATEGORY_BILLS_ELECTRICITY=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Prąd\", \"parentCategoryId\": $CATEGORY_BILLS}")
echo "$CATEGORY_BILLS_ELECTRICITY"

CATEGORY_BILLS_GARBAGE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Śmieci\", \"parentCategoryId\": $CATEGORY_BILLS}")
echo "$CATEGORY_BILLS_GARBAGE"

CATEGORY_BILLS_WATER=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Woda\", \"parentCategoryId\": $CATEGORY_BILLS}")
echo "$CATEGORY_BILLS_WATER"

CATEGORY_TRANSFER=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Przelewy między kontami\"}")
echo "$CATEGORY_TRANSFER"

CATEGORY_MONTH_SUMMARY=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Rozliczenie miesiąca\"}")
echo "$CATEGORY_MONTH_SUMMARY"

CATEGORY_FOOD_OUTSIDE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Jedzenie\"}")
echo "$CATEGORY_FOOD_OUTSIDE"

CATEGORY_DIVING=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Nurkowanie\"}")
echo "$CATEGORY_DIVING"

CATEGORY_HEALTH=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Leki, lekarze, higiena\"}")
echo "$CATEGORY_HEALTH"

CATEGORY_SPORT=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Sport\"}")
echo "$CATEGORY_SPORT"

CATEGORY_COSMETICS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Kosmetyki\"}")
echo "$CATEGORY_COSMETICS"

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

CATEGORY_EDUCATION=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Edukacja\"}")
echo "$CATEGORY_EDUCATION"

CATEGORY_SHOPPING=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Zakupy\"}")
echo "$CATEGORY_SHOPPING"

CATEGORY_SHOPPING_FOOD=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Zakupy - jedzenie\"}, \"parentCategoryId\": $CATEGORY_SHOPPING}")
echo "$CATEGORY_SHOPPING_FOOD"

CATEGORY_HOME_RENOVATION=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Remont domu\"}")
echo "$CATEGORY_HOME_RENOVATION"

CATEGORY_INCOME_CROSSOVER=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Crossover\", \"parentCategoryId\": $CATEGORY_INCOME}")
echo "$CATEGORY_INCOME_CROSSOVER"

CATEGORY_INCOME_CODERSTRUST=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"CodersTrust - szkolenia\", \"parentCategoryId\": $CATEGORY_INCOME}")
echo "$CATEGORY_INCOME_CODERSTRUST"

CATEGORY_INCOME_CODERSTRUST_LICENSE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"CodersTrust - licencja\", \"parentCategoryId\": $CATEGORY_INCOME}")
echo "$CATEGORY_INCOME_CODERSTRUST_LICENSE"

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
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-04\", \"description\": \"Crossover 18-24.12\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_OTHER, \"date\": \"2018-01-04\", \"description\": \"Idea Bank Premia\", \"price\": 50.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-01-05\", \"description\": \"ZUS - Styczeń\", \"price\": -473.20}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_INCOME_TAX, \"date\": \"2018-01-15\", \"description\": \"Podatek dochodowy Q4 2017\", \"price\": -5746.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_VAT, \"date\": \"2018-01-15\", \"description\": \"VAT Q4 2017\", \"price\": -759}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-18\", \"description\": \"Crossover Interviews - Grudzień\", \"price\": 985.19}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-03\", \"description\": \"USD -> EUR\", \"price\": -3582.51}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-03\", \"description\": \"USD -> EUR\", \"price\": 3513.57}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-03\", \"description\": \"USD -> EUR - Ania zwrot\", \"price\": 34.47}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-06\", \"description\": \"Noclegi Gran Canaria\", \"price\": -4418.60}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-06\", \"description\": \"Noclegi Gran Canaria - Ania zwrot\", \"price\": 2209.30}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-01-07\", \"description\": \"Nurkowanie - Top Diving Gran Canaria\", \"price\": -542.72}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_FOOD_OUTSIDE, \"date\": \"2018-01-06\", \"description\": \"Jedzenie\", \"price\": -195.04}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_FOOD_OUTSIDE, \"date\": \"2018-01-06\", \"description\": \"Jedzenie - Ania zwrot\", \"price\": 97.52}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-01-08\", \"description\": \"Audioteka\", \"price\": -19.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-10\", \"description\": \"Noclegi Fuerteventura - zaliczka\", \"price\": 979.23}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSPORT, \"date\": \"2018-01-10\", \"description\": \"Prom Fuerteventura\", \"price\": -565.28}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-06\", \"description\": \"Prom Fuerteventura\", \"price\": 282.64}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-01-14\", \"description\": \"Nurkowanie - Las Palmas Gran Canaria\", \"price\": -468.31}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-13\", \"description\": \"USD -> EUR\", \"price\": -985.18}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-01-13\", \"description\": \"USD -> EUR\", \"price\": 950.14}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-01-09\", \"description\": \"Księgowy - Styczeń\", \"price\": -123.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-01-26\", \"description\": \"Leasing - styczeń\", \"price\": -2308.97}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-01-26\", \"description\": \"Leasing - styczeń - Ania zwrot\", \"price\": 1154.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-01-18\", \"description\": \"Telefon\", \"price\": -21.34}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-01-26\", \"description\": \"Paliwo\", \"price\": -239.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-01-26\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 119.75}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-01-26\", \"description\": \"Zakupy\", \"price\": -150}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-01-26\", \"description\": \"Zakupy - Ania zwrot\", \"price\": 75}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-18\", \"description\": \"Crossover 01-07.01\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_FOOD_OUTSIDE, \"date\": \"2018-01-06\", \"description\": \"Jedzenie\", \"price\": -135.68}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_FOOD_OUTSIDE, \"date\": \"2018-01-06\", \"description\": \"Jedzenie - Ania zwrot\", \"price\": 67.84}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-10\", \"description\": \"Noclegi Fuerteventura \", \"price\": -979.23}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-01-21\", \"description\": \"Nurkowanie - North Gran Canaria\", \"price\": -148.40}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-01-25\", \"description\": \"Crossover 08-14.01\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Przelew Alior PLN\", \"price\": -27.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_PLN, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Przelew Alior PLN\", \"price\": 27.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Przelew Ania PLN\", \"price\": -4398.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Przelew Ania PLN\", \"price\": 4398.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Przelew Ania PLN\", \"price\": 546.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Przelew Ania PLN\", \"price\": -546.9}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Gotowka Ania EUR\", \"price\": 21.57}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-01-25\", \"description\": \"Gotowka Ania EUR\", \"price\": -21.57}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CHILD, \"date\": \"2018-01-31\", \"description\": \"Wydatki na dziecko\", \"price\": -34.11}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_BILLS_TV, \"date\": \"2018-01-31\", \"description\": \"Abonament RTV\", \"price\": -122.57}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_BILLS_GAS, \"date\": \"2018-01-31\", \"description\": \"Gaz\", \"price\": -233.21}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_BILLS_INTERNET, \"date\": \"2018-01-31\", \"description\": \"Internet\", \"price\": -34.44}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_BILLS_ELECTRICITY, \"date\": \"2018-01-31\", \"description\": \"Prąd\", \"price\": -258.13}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_BILLS_GARBAGE, \"date\": \"2018-01-31\", \"description\": \"Śmieci\", \"price\": -15}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_BILLS_WATER, \"date\": \"2018-01-31\", \"description\": \"Woda\", \"price\": -148.32}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR, \"date\": \"2018-01-31\", \"description\": \"Wymiana szyby\", \"price\": -1351.79}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR, \"date\": \"2018-01-31\", \"description\": \"Inne wydatki\", \"price\": -20.15}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-01-31\", \"description\": \"Paliwo\", \"price\": -125.25}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR, \"date\": \"2018-01-31\", \"description\": \"Parkingi\", \"price\": -15.42}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSPORT, \"date\": \"2018-01-31\", \"description\": \"Promy\", \"price\": -254.36}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ACCOMMODATION, \"date\": \"2018-01-31\", \"description\": \"Noclegi Fuerta\", \"price\": -2064.96}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_FOOD_OUTSIDE, \"date\": \"2018-01-31\", \"description\": \"Jedzenie - restauracje\", \"price\": -342.33}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING_FOOD, \"date\": \"2018-01-31\", \"description\": \"Jedzenie - zakupy\", \"price\": -667.52}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HEALTH, \"date\": \"2018-01-31\", \"description\": \"Lekarstwa\", \"price\": -166.72}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HEALTH, \"date\": \"2018-01-31\", \"description\": \"Kosmetyki\", \"price\": -10.97}"

# Transactions - February

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_SHOPPING_FOOD, \"date\": \"2018-02-01\", \"description\": \"Zakupy\", \"price\": -142.51}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING_FOOD, \"date\": \"2018-02-01\", \"description\": \"Zakupy - Ania zwrot\", \"price\": 71.26}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR, \"date\": \"2018-02-20\", \"description\": \"Zwrot za wymiane szyby\", \"price\": 2675.64}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR, \"date\": \"2018-02-20\", \"description\": \"Zwrot za wymiane szyby\", \"price\": -1337.82}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-02-05\", \"description\": \"ZUS - Luty\", \"price\": -504.66}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_OTHER, \"date\": \"2018-02-04\", \"description\": \"Idea Bank Premia\", \"price\": 30.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-02-07\", \"description\": \"Nurkowanie - Fuertaventura\", \"price\": -763.20}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-02-16\", \"description\": \"Paliwo\", \"price\": -274.24}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-02-16\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 137.12}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-02-07\", \"description\": \"Nurkowanie - ubezpieczenie\", \"price\": -364.64}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-02-03\", \"description\": \"USD -> EUR\", \"price\": -10747.5}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-02-03\", \"description\": \"USD -> EUR\", \"price\": 10281.87}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-02-04\", \"description\": \"Crossover 15-21.01\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-02-09\", \"description\": \"Księgowy - Luty\", \"price\": -123.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-08\", \"description\": \"Audioteka\", \"price\": -19.90}"

# TODO split (kaktusy+papagayo+ferry+air)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-08\", \"description\": \"Zwiedzanie - Papagayo, kaktusy, prom itp\", \"price\": -1911.71}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-08\", \"description\": \"Zwiedzanie - Papagayo, kaktusy itp\", \"price\": 955.85}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-02-14\", \"description\": \"Crossover 22-28.01\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-02-14\", \"description\": \"Crossover 29.01-04.02\", \"price\": 7165.00}"

# TODO split (paliwo, noclegi itp)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-08\", \"description\": \"paliwo noclegi itp\", \"price\": -3114.99}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-08\", \"description\": \"paliwo noclegi itp\", \"price\": 1557.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-02-18\", \"description\": \"Telefon\", \"price\": -22.75}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-02-09\", \"description\": \"Księgowy - rozliczenie roku\", \"price\": -61.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-02-07\", \"description\": \"Nurkowanie \", \"price\": -279.94}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_CODERSTRUST, \"date\": \"2018-02-28\", \"description\": \"CT - styczen\", \"price\": 14083.30}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-02-14\", \"description\": \"Crossover 05-11.02\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-02-26\", \"description\": \"Leasing - luty\", \"price\": -2308.97}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-02-26\", \"description\": \"Leasing - luty - Ania zwrot\", \"price\": 1154.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-02-28\", \"description\": \"Crossover 12-18.02\", \"price\": 7165.00}"

# TODO split (paliwo, promy, noclegi)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-28\", \"description\": \"paliwo noclegi itp\", \"price\": -2336.79}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-28\", \"description\": \"paliwo noclegi itp\", \"price\": 1168.4}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-02-27\", \"description\": \"Nurkowanie\", \"price\": -118.72}"

#TODO split (zycie, rozliczenie miesiaca)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-02-25\", \"description\": \"Rozliczenie miesiaca\", \"price\": -1202.78}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Przelew Ania PLN\", \"price\": 490.94}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Przelew Ania PLN\", \"price\": -490.94}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Monika pożyczka\", \"price\": -8400}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_MONIKA_LOAN, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Monika pożyczka\", \"price\": 8400}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Przelew Ania PLN\", \"price\": 1709.68}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Przelew Ania PLN\", \"price\": -1709.68}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Przelew Ania PLN\", \"price\": -23.32}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-02-25\", \"description\": \"Przelew Ania PLN\", \"price\": 23.32}"

# Transactions - March

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_OTHER, \"date\": \"2018-03-04\", \"description\": \"Idea Bank Premia\", \"price\": 30.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-03-09\", \"description\": \"Księgowy - marzec\", \"price\": -123.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-03-05\", \"description\": \"ZUS - marzec\", \"price\": -504.66}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-08\", \"description\": \"Audioteka\", \"price\": -19.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_INCOME_TAX, \"date\": \"2018-03-08\", \"description\": \"Zwrot podatku (IKZE)\", \"price\": 972.0}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-03-16\", \"description\": \"Leasing - marzec\", \"price\": -2308.97}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-03-16\", \"description\": \"Leasing - marzec - Ania zwrot\", \"price\": 1154.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_CODERSTRUST, \"date\": \"2018-03-15\", \"description\": \"CT - luty\", \"price\": 14041.2}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-03-08\", \"description\": \"Crossover 19-25.02\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-03-07\", \"description\": \"USD -> EUR\", \"price\": -3582.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-03-07\", \"description\": \"USD -> EUR\", \"price\": 3460.07}"

#TODO split (paliwo promy itp)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-15\", \"description\": \"Paliwo, promy itp\", \"price\": -4031.18}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-15\", \"description\": \"Paliwo, promy itp\", \"price\": 2015.59}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_SHOPPING_FOOD, \"date\": \"2018-03-06\", \"description\": \"Jedzenie\", \"price\": -113.99}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING_FOOD, \"date\": \"2018-03-06\", \"description\": \"Jedzenie - Ania zwrot\", \"price\": 57.0}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-03-18\", \"description\": \"Telefon\", \"price\": -24.31}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-03-15\", \"description\": \"Crossover 16.02-04.03\", \"price\": 7165.00}"

#TODO split (zakupy, paliwo itp)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-15\", \"description\": \"zakupy, paliwo itp\", \"price\": -2514.13}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-15\", \"description\": \"zakupy, paliwo itp\", \"price\": 1257.06}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-03-19\", \"description\": \"Księgowy - rozliczenie drugi PIT\", \"price\": -61.50}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-03-22\", \"description\": \"Crossover 05-11.03\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-03-29\", \"description\": \"Crossover 12-18.03\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH, \"categoryId\": $CATEGORY_HOME_MAINTENANCE, \"date\": \"2018-03-25\", \"description\": \"kominiarz\", \"price\": -120}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HOME_MAINTENANCE, \"date\": \"2018-03-25\", \"description\": \"kominiarz\", \"price\": 60}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-28\", \"description\": \"Safari Egipt zaliczka\", \"price\": -1660}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-03-25\", \"description\": \"Projekt przebudowy domu\", \"price\": -9500}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-03-25\", \"description\": \"Projekt przebudowy domu\", \"price\": 4750}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_COSTS, \"date\": \"2018-03-28\", \"description\": \"Kabel HDMI\", \"price\": -191.84}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_SPORT, \"date\": \"2018-03-28\", \"description\": \"Decathlon - zele energetyczne itp\", \"price\": -150}"

#TODO split (zycie, rozliczenie miesiaca)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-03-15\", \"description\": \"Rozliczenie miesiaca - marzec\", \"price\": -3779.37}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-03-25\", \"description\": \"Przelew Ania PLN\", \"price\": 1442.26}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-03-25\", \"description\": \"Przelew Ania PLN\", \"price\": -1442.26}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-03-25\", \"description\": \"Przelew Ania PLN\", \"price\": -363.29}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-03-25\", \"description\": \"Przelew Ania PLN\", \"price\": 363.29}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-03-25\", \"description\": \"Przelew Ania PLN\", \"price\": -60.76}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-03-25\", \"description\": \"Przelew Ania PLN\", \"price\": 60.76}"

# Transactions - April

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-04-09\", \"description\": \"Księgowy - Kwiecien\", \"price\": -123.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_OTHER, \"date\": \"2018-04-04\", \"description\": \"Idea Bank Premia\", \"price\": 50.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-04-04\", \"description\": \"Crossover 19-25.03\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_INCOME_TAX, \"date\": \"2018-04-15\", \"description\": \"Podatek dochodowy Q1 2018\", \"price\": -21369.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_VAT, \"date\": \"2018-04-15\", \"description\": \"VAT Q1 2018\", \"price\": -7320}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-04-05\", \"description\": \"ZUS - marzec\", \"price\": -504.66}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-04-08\", \"description\": \"Audioteka\", \"price\": -19.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-04-06\", \"description\": \"Zakupy\", \"price\": -191.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-04-06\", \"description\": \"Zakupy - Ania zwrot\", \"price\": 105.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSPORT, \"date\": \"2018-04-10\", \"description\": \"Bilety Warszawa\", \"price\": -289}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-04-13\", \"description\": \"USD -> PLN\", \"price\": -28659.96}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-04-13\", \"description\": \"USD -> PLN\", \"price\": 27279.68}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_CODERSTRUST, \"date\": \"2018-04-15\", \"description\": \"CT - marzec\", \"price\": 11433}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-04-12\", \"description\": \"Crossover 26.03-1.04\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-04-16\", \"description\": \"Paliwo\", \"price\": -286.32}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-04-16\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 143.16}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSPORT, \"date\": \"2018-04-10\", \"description\": \"Bilety Warszawa - nowe\", \"price\": -135}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSPORT, \"date\": \"2018-04-10\", \"description\": \"Bilety Warszawa - zwroty\", \"price\": 360.40}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-04-26\", \"description\": \"Leasing - kwiecien\", \"price\": -2308.97}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-04-26\", \"description\": \"Leasing - kwiecien - Ania zwrot\", \"price\": 1154.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-04-18\", \"description\": \"Telefon\", \"price\": -19.89}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_EDUCATION, \"date\": \"2018-04-18\", \"description\": \"Ksiazki\", \"price\": -89.80}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-04-25\", \"description\": \"Wizualizacja projektu\", \"price\": -1500}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-04-25\", \"description\": \"Wizualizacja projektu\", \"price\": 750}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-04-19\", \"description\": \"Crossover 02.04-08.04\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-04-26\", \"description\": \"Paliwo\", \"price\": -278.36}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-04-26\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 139.18}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_SPORT, \"date\": \"2018-04-26\", \"description\": \"Bieg po puszczy\", \"price\": -65}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_EDUCATION, \"date\": \"2018-04-28\", \"description\": \"Szkolenie - Motopark\", \"price\": -499.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-04-26\", \"description\": \"Crossover 09.04-15.04\", \"price\": 7165.00}"

#TODO split (zycie, rozliczenie miesiaca)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-04-15\", \"description\": \"Rozliczenie miesiaca - marzec\", \"price\": -5183.4}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-04-10\", \"description\": \"Przelew Ania PLN\", \"price\": 200.38}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-04-10\", \"description\": \"Przelew Ania PLN\", \"price\": -200.38}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-04-30\", \"description\": \"Przelew Ania PLN\", \"price\": -1174.5}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-04-30\", \"description\": \"Przelew Ania PLN\", \"price\": 1174.5}"

# Transactions - May

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-05-09\", \"description\": \"Crossover 16-22.04\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH, \"categoryId\": $CATEGORY_HEALTH, \"date\": \"2018-05-09\", \"description\": \"Fryzjer\", \"price\": 33.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-05-13\", \"description\": \"USD -> EUR\", \"price\": -5373.75}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-05-13\", \"description\": \"USD -> EUR\", \"price\": 5290.75}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-05-13\", \"description\": \"USD -> PLN\", \"price\": -12602.66}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-05-13\", \"description\": \"USD -> PLN\", \"price\": 12450.55}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-05-26\", \"description\": \"Balkony - zaliczka\", \"price\": -5680}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-05-26\", \"description\": \"Balkony - zaliczka\", \"price\": 2840}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-05-05\", \"description\": \"ZUS - Maj\", \"price\": -1163.39}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-05-08\", \"description\": \"Audioteka\", \"price\": -19.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-05-09\", \"description\": \"Księgowy - Maj\", \"price\": -132.23}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-05-09\", \"description\": \"Crossover 23-29.04\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-05-09\", \"description\": \"Egipt - Euro\", \"price\": -3392}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_CODERSTRUST, \"date\": \"2018-05-28\", \"description\": \"CT - kwiecien\", \"price\": 12982.5}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_HEALTH, \"date\": \"2018-05-19\", \"description\": \"Badania i leki\", \"price\": -107.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_SPORT, \"date\": \"2018-05-28\", \"description\": \"Decathlon - zele energetyczne itp\", \"price\": -129.97}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-05-26\", \"description\": \"Okna - zaliczka\", \"price\": -13500}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_HOME_RENOVATION, \"date\": \"2018-05-26\", \"description\": \"Okna - zaliczka\", \"price\": 6750}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-05-17\", \"description\": \"Crossover 30.04-06.05\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-05-15\", \"description\": \"Klub wysokogorski - skladka\", \"price\": -100}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-05-26\", \"description\": \"Leasing - maj\", \"price\": -2308.97}"

#curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-05-26\", \"description\": \"Leasing - maj - Ania zwrot\", \"price\": 1154.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-05-18\", \"description\": \"Telefon\", \"price\": -19.72}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-05-15\", \"description\": \"Egipt - paliwo, parking, jedzenie\", \"price\": -441.09}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-05-15\", \"description\": \"Egipt - paliwo, parking, jedzenie - Ania zwrot\", \"price\": 162.69}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-05-15\", \"description\": \"Jedzenie dla kota\", \"price\": 109.44}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_SHOPPING, \"date\": \"2018-05-15\", \"description\": \"Jedzenie dla kota\", \"price\": -109.44}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_CODERSTRUST_LICENSE, \"date\": \"2018-05-15\", \"description\": \"CT - licencja - kurs Java\", \"price\": 1633.44}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-05-24\", \"description\": \"Crossover 07-13.05\", \"price\": 7165.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-05-31\", \"description\": \"Crossover 14-20.05\", \"price\": 7165.00}"

#TODO split (zycie, rozliczenie miesiaca)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-05-15\", \"description\": \"Rozliczenie miesiaca - maj\", \"price\": -4050.15}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-10\", \"description\": \"Przelew Ania PLN\", \"price\": -50.45}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-10\", \"description\": \"Przelew Ania PLN\", \"price\": 50.45}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": 63.87}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": -63.87}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": 640.81}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": -640.81}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": -63}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": 63}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH_EUR, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": -4.24}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-05-30\", \"description\": \"Przelew Ania PLN\", \"price\": 4.24}"

# Transactions - June

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-06\", \"description\": \"Paliwo\", \"price\": -268.46}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-06\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 134.23}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ZUS, \"date\": \"2018-06-05\", \"description\": \"ZUS - Maj\", \"price\": -1163.39}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-06-01\", \"description\": \"Przelew Ania PLN\", \"price\": -1577.42}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_TRANSFER, \"date\": \"2018-06-01\", \"description\": \"Przelew Ania PLN\", \"price\": 1577.42}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-06-08\", \"description\": \"Audioteka\", \"price\": -19.90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_SPORT, \"date\": \"2018-06-28\", \"description\": \"Zawody - bieg 3 kopcow, Cracovia Polmaraton\", \"price\": -100}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_EDUCATION, \"date\": \"2018-06-18\", \"description\": \"Ksiazki\", \"price\": -179.52}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_EDUCATION, \"date\": \"2018-06-18\", \"description\": \"Ksiazki - Ania\", \"price\": 89.76}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_HOME_MAINTENANCE, \"date\": \"2018-06-18\", \"description\": \"Kluczyk do szafki Kinarps\", \"price\": -30.75}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-06-18\", \"description\": \"Crossover Lodz Meetup - zwrot za paliwo\", \"price\": 32}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-06\", \"description\": \"Paliwo\", \"price\": -292.27}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-06\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 146.14}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_ACCOUNTANT, \"date\": \"2018-06-09\", \"description\": \"Księgowy - czerwiec\", \"price\": -123}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_EDUCATION, \"date\": \"2018-06-28\", \"description\": \"Szkolenie - Motopark - 2 stopien\", \"price\": -499.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_INCOME_CODERSTRUST, \"date\": \"2018-06-28\", \"description\": \"CT - maj\", \"price\": 9724.68}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_COSTS, \"date\": \"2018-06-28\", \"description\": \"Sluchawki\", \"price\": -530.69}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-06-07\", \"description\": \"Crossover (21-27.05) - urlop\", \"price\": 0}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-06-14\", \"description\": \"Crossover (28.05-03.06)\", \"price\": 7165}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-06-26\", \"description\": \"Leasing - czerwiec\", \"price\": -2308.97}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_LEASING, \"date\": \"2018-06-26\", \"description\": \"Leasing - czerwiec - Ania zwrot\", \"price\": 1154.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_PHONE, \"date\": \"2018-06-18\", \"description\": \"Telefon\", \"price\": -31.49}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_COSTS, \"date\": \"2018-06-28\", \"description\": \"Domeny\", \"price\": -55.92}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-06\", \"description\": \"Paliwo\", \"price\": -311.31}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-06\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 155.66}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_EDUCATION, \"date\": \"2018-06-28\", \"description\": \"Szkolenie - KPP\", \"price\": -650.00}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_COSTS, \"date\": \"2018-06-28\", \"description\": \"Ladowarka USB\", \"price\": -97.60}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR, \"date\": \"2018-06-06\", \"description\": \"Mandat - Francja\", \"price\": -252.30}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-06-21\", \"description\": \"Crossover (04-10.06)\", \"price\": 7165}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-06-13\", \"description\": \"USD -> PLN\", \"price\": -21495}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-06-13\", \"description\": \"USD -> PLN\", \"price\": 22376.28}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_EUR, \"categoryId\": $CATEGORY_DIVING, \"date\": \"2018-06-07\", \"description\": \"Nurkowanie - Zakrzowek\", \"price\": -90}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-26\", \"description\": \"Paliwo\", \"price\": -294.23}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2018-06-26\", \"description\": \"Paliwo - Ania zwrot\", \"price\": 147.12}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_COMPANY_COSTS, \"date\": \"2018-06-26\", \"description\": \"Nozbe\", \"price\": -288}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_COMPANY_COSTS, \"date\": \"2018-06-26\", \"description\": \"Nozbe - Ania zwrot\", \"price\": 144}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-06-13\", \"description\": \"USD -> PLN\", \"price\": -64073.02}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_PLN, \"categoryId\": $CATEGORY_CURRENCY_EXCHANGE, \"date\": \"2018-06-13\", \"description\": \"USD -> PLN\", \"price\": 67185.34}"

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-06-28\", \"description\": \"Crossover (11-17.06)\", \"price\": 7111.26}"

#TODO split (zycie, rozliczenie miesiaca)
curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ANIA_BALANCE, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-06-30\", \"description\": \"Rozliczenie miesiaca - czerwiec\", \"price\": -4146.78}"

# note - only final accounts sum state is correct - accounts are not balanced - no need for that now.

curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_ALIOR_USD, \"categoryId\": $CATEGORY_INCOME_CROSSOVER, \"date\": \"2018-07-05\", \"description\": \"Crossover (18-24.06)\", \"price\": 7111.26}"
