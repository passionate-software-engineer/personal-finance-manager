CREATE TABLE transaction
(
  id          SERIAL PRIMARY KEY,
  description VARCHAR(255)   NOT NULL,
  price       NUMERIC(19, 2) NOT NULL,
  category_id VARCHAR(255)   NOT NULL REFERENCES category (id),
  account_id  VARCHAR(255)   NOT NULL REFERENCES account (id)
);