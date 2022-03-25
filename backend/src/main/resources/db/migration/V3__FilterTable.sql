CREATE TABLE filter
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price_from  NUMERIC(19, 2),
    price_to    NUMERIC(19, 2),
    date_from   DATE,
    date_to     DATE
);

CREATE TABLE filter_account_ids
(
    filter_id  BIGINT,
    account_id BIGINT REFERENCES account (id)
);

CREATE TABLE filter_category_ids
(
    filter_id   BIGINT,
    category_id BIGINT REFERENCES category (id)
);

