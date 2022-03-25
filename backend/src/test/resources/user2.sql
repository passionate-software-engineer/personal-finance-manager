-- account ---------------------------------------------

SELECT account.name,
       account.balance,
       currency.name                  AS currency,
       account.last_verification_date AS balance_verification_date,
       account.archived
FROM account
         LEFT JOIN currency ON account.currency_id = currency.id
WHERE account.user_id = :userB
GROUP BY account.name, account.balance, currency.name, account.last_verification_date,
         account.archived;

-- history ---------------------------------------------

SELECT date,
    type,
    object,
    name,
    old_value,
    new_value
FROM history_entry
    LEFT JOIN history_entry_entries
ON history_entry.id = history_entry_id
    LEFT JOIN history_info hi ON history_entry_entries.entries_id = hi.id
WHERE user_id = :userB;

-- transaction -------------------------------------------

SELECT date,
    description,
    ape.price,
    a.name AS account,
    c.name AS category
FROM transaction
    JOIN category c
ON transaction.category_id = c.id
    JOIN transaction_account_price_entries tape ON transaction.id = tape.transaction_id
    JOIN account_price_entry ape ON tape.account_price_entries_id = ape.id
    JOIN account a ON ape.account_id = a.id
WHERE a.user_id = :userB;

-- categories -----------------------------------------------

SELECT c2.name,
       c1.name AS parent_category
FROM category c1
         JOIN category c2 ON c2.parent_category_id = c1.id
WHERE c1.user_id = :userB;

-- parent_category = null ---------------------------------------

SELECT name,
       parent_category_id
FROM category
WHERE parent_category_id ISNULL
  AND user_id = :userB;

-- filter ------------------------------------------------------------------------------------------

SELECT filter.name,
       filter.description,
       filter.price_from,
       filter.price_to,
       filter.date_from,
       filter.date_to
FROM filter
         JOIN app_user au ON filter.user_id = au.id
WHERE user_id = :userB;

