CREATE TABLE account
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255)   NOT NULL,
    balance NUMERIC(19, 2) NOT NULL
);

CREATE TABLE category
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    parent_category_id BIGINT REFERENCES category (id)
);
