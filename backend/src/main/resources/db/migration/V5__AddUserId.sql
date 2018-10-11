ALTER TABLE account
ADD user_id BIGINT REFERENCES userek (id);

ALTER TABLE category
ADD user_id BIGINT REFERENCES userek (id);

ALTER TABLE transaction
ADD user_id BIGINT REFERENCES userek (id);

ALTER TABLE filter
ADD user_id BIGINT REFERENCES userek (id);

CREATE INDEX idx_user_id_account_table
ON account (user_id);

CREATE INDEX idx_user_id_category_table
ON category (user_id);

CREATE INDEX idx_user_id_transaction_table
ON transaction (user_id);

CREATE INDEX idx_user_id_filter_table
ON filter (user_id);
