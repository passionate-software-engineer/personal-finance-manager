package com.pfm.database;

public class TestSQLQueries {

  public static final String SELECT_ALL_ACCOUNTS = "SELECT * FROM account WHERE user_id =";

  public static final String SELECT_ALL_HISTORY =
      "SELECT date,\n"
          + "       type,\n"
          + "       object,\n"
          + "       name,\n"
          + "       old_value,\n"
          + "       new_value\n"
          + "\n"
          + "FROM history_entry\n"
          + "       LEFT JOIN history_entry_entries ON history_entry.id = history_entry_id\n"
          + "       LEFT JOIN history_info hi ON history_entry_entries.entries_id = hi.id\n"
          + "WHERE user_id =";

  public static final String SELECT_ALL_TRANSACTIONS =
      "SELECT date,\n"
          + "       description,\n"
          + "       ape.price,\n"
          + "       a.name AS account,\n"
          + "       c.name AS category\n"
          + "FROM transaction\n"
          + "       JOIN category c ON transaction.category_id = c.id\n"
          + "       JOIN transaction_account_price_entries tape ON transaction.id = tape.transaction_id\n"
          + "       JOIN account_price_entry ape ON tape.account_price_entries_id = ape.id\n"
          + "       JOIN account a ON ape.account_id = a.id\n"
          + "WHERE a.user_id =";

  public static final String SELECT_ALL_CATEGORIES =
      "SELECT c2.name,\n"
          + "       c1.name AS parent_category\n"
          + "FROM category c1\n"
          + "       JOIN category c2 ON c2.parent_category_id = c1.id\n"
          + "WHERE c1.user_id =";

  public static final String SELECT_MAIN_PARENT_CATEGORY_CATEGORIES =
     "SELECT name FROM category WHERE parent_category_id IS NULL  AND user_id =";

  public static final String SELECT_ALL_FILTERS =

      "SELECT filter.name,\n"
      +"       filter.description,\n"
      +"       filter.price_from,\n"
      +"       filter.price_to,\n"
      +"       filter.date_from,\n"
      +"       filter.date_to\n"
      +"FROM filter\n"
      +"       JOIN app_user au ON filter.user_id = au.id\n"
      +"WHERE user_id = ";



  public static final String ACCOUNTS_TO_MATCH_FRONTEND_TABLE_LAYOUT_FOR_GIVEN_USER_ID =
      "SELECT account.name,\n"
          + "       account.balance,\n"
          + "       currency.name                             AS currency,\n"
          + "       round(account.balance * exchange_rate, 2) AS balance_PLN,\n"
          + "       account.last_verification_date            AS balance_verification_date,\n"
          + "       account.archived\n"
          + "FROM account\n"
          + "       LEFT JOIN currency ON account.currency_id = currency.id\n"
          + "WHERE account.user_id =";

}
