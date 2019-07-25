package com.pfm.database;

public class TestSQLQueries {

  public static final String SELECT_ALL_ACCOUNTS_WHERE_USER_ID = "SELECT * FROM account WHERE user_id =";

  public static final String SELECT_ALL_HISTORY_WHERE_USER_ID = "\n"
      + "SELECT date,\n"
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
