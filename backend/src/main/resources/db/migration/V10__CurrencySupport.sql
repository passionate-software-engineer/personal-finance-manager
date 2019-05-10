CREATE TABLE currency
(
  id            BIGSERIAL PRIMARY KEY,
  name          VARCHAR(50)    NOT NULL,
  exchange_rate NUMERIC(19, 2) NOT NULL,
  user_id       BIGINT REFERENCES app_user (id)
);

ALTER TABLE account
  ADD currency_id BIGINT REFERENCES currency (id);
