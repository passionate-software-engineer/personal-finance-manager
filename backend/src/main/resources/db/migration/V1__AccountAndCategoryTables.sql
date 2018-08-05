CREATE TABLE account
(
  id      SERIAL PRIMARY KEY,
  name    VARCHAR(255)   NOT NULL,
  balance NUMERIC(19, 2) NOT NULL
);

CREATE TABLE category
(
  id                 SERIAL PRIMARY KEY,
  name               VARCHAR(255) NOT NULL,
  parent_category_id BIGINT REFERENCES category (id)
);
