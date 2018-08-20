CREATE TABLE filter
(
  id           SERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(255) NOT NULL,
  price_from    NUMERIC(19, 2),
  price_to      NUMERIC(19, 2),
  date_from     DATE,
  date_to       DATE
);

CREATE TABLE filter_accounts_ids
(
  filter_id           BIGINT,
  accounts_ids         BIGINT
);

CREATE TABLE filter_categories_ids
(
  filter_id           BIGINT,
  categories_ids       BIGINT
);

