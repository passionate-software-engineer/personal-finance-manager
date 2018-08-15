CREATE TABLE filter
(
  id           SERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(255) NOT NULL,
  price_from    NUMERIC(19, 2),
  price_to      NUMERIC(19, 2)
);

CREATE TABLE filter_accounts
(
  filter_id           BIGINT,
  accounts_id         BIGINT
);

CREATE TABLE filter_categories
(
  filter_id           BIGINT,
  categories_id         BIGINT
);

