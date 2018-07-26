CREATE TABLE account
(
  id SERIAL PRIMARY KEY ,
  name VARCHAR(255) NOT NULL ,
  balance NUMERIC(19,2) NOT NULL
);

CREATE TABLE category
(
  id SERIAL PRIMARY KEY ,
  name VARCHAR(255) NOT NULL ,
  parent_category_id BIGINT REFERENCES category (id)
);

CREATE TABLE transaction
(
  id SERIAL PRIMARY KEY ,
  description VARCHAR(255) NOT NULL ,
  category VARCHAR(255) NOT NULL ,
  price NUMERIC(19,2) NOT NULL,
  account VARCHAR(255) NOT NULL ,
);