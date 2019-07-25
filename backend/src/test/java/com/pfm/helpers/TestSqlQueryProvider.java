package com.pfm.helpers;

public class TestSqlQueryProvider {

  public static final String COMPARE_ACCOUNT_TABLES =
      "SELECT *\n"
          + "FROM (\n"
          + "       SELECT account.name,\n"
          + "              account.balance,\n"
          + "              currency.name                             AS currency,\n"
          + "              round(account.balance * exchange_rate, 2) AS balance_PLN,\n"
          + "              account.last_verification_date            AS balance_verification_date,\n"
          + "              account.archived\n"
          + "       FROM account\n"
          + "              LEFT JOIN currency ON account.currency_id = currency.id\n"
          + "       WHERE account.user_id = 1\n"
          + "       UNION ALL\n"
          + "       SELECT account.name                              AS account_name,\n"
          + "              account.balance,\n"
          + "              currency.name                             AS currency,\n"
          + "              round(account.balance * exchange_rate, 2) AS balance_PLN,\n"
          + "              account.last_verification_date            AS balance_verification_date,\n"
          + "              account.archived\n"
          + "       FROM account\n"
          + "              LEFT JOIN currency ON account.currency_id = currency.id\n"
          + "       WHERE account.user_id = 2\n"
          + "     ) subq\n"
          + "GROUP BY subq.name, subq.balance, subq.currency, subq.balance_PLN,\n"
          + "         subq.balance_verification_date,\n"
          + "         subq.archived\n"
          + "HAVING COUNT(*) = 1";

  public static final String SELECT_ALL_ACCOUNTS =
      "SELECT * FROM ACCOUNT";

  public static final String SELECT_ACCOUNT_FOR_USER_NO_4 =
      "SELECT name, balance,last_verification_date,archived "
          + "from account  where user_id = 4";

}
