CREATE TABLE account_price_entry
(
  id         BIGSERIAL PRIMARY KEY,
  account_id BIGINT REFERENCES account (id),
  price      NUMERIC(19, 2) NOT NULL
);

CREATE TABLE transaction_account_price_entries
(
  transaction_id           BIGINT REFERENCES transaction (id),
  account_price_entries_id BIGINT REFERENCES account_price_entry (id)
);

ALTER TABLE transaction
  DROP COLUMN price;

ALTER TABLE transaction
  DROP COLUMN account_id;
