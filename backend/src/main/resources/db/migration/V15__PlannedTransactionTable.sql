CREATE TABLE planned_transaction
(
  id          BIGSERIAL PRIMARY KEY,
  category_id BIGINT REFERENCES category (id),
  user_id     bigint REFERENCES app_user (id),
  description VARCHAR(255) NOT NULL,
  date        DATE         NOT NULL

);

CREATE TABLE planned_transaction_account_price_entries
(
  planned_transaction_id   BIGINT REFERENCES planned_transaction (id),
  account_price_entries_id BIGINT REFERENCES account_price_entry (id)
);
