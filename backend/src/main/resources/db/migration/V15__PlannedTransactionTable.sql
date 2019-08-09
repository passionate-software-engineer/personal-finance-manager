CREATE TABLE planned_transaction
(
  id          BIGSERIAL PRIMARY KEY,
  description VARCHAR(255)    NULL,
  category_id BIGINT REFERENCES category (id),
  account_id  BIGINT REFERENCES account (id),
  price       NUMERIC(19, 2)  NULL,
  due_date    DATE            NULL
);

CREATE TABLE planned_transaction_account_price_entries
(
  planned_transaction_id   BIGINT REFERENCES planned_transaction (id),
  account_price_entries_id BIGINT REFERENCES account_price_entry (id)
);

ALTER TABLE planned_transaction
  DROP COLUMN price;

ALTER TABLE planned_transaction
  DROP COLUMN account_id;
