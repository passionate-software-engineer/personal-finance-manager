CREATE TABLE transaction
(
  id          BIGSERIAL PRIMARY KEY,
  description VARCHAR(255)   NOT NULL,
  category_id BIGINT REFERENCES category (id),
  account_id  BIGINT REFERENCES account (id),
  price       NUMERIC(19, 2) NOT NULL,
  date        DATE           NOT NULL,
  user_id     BIGINT         NOT NULL
);