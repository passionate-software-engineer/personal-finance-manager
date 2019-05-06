CREATE UNIQUE INDEX account_id_index
    ON account (id);

CREATE UNIQUE INDEX category_id_index
    ON category (id);

CREATE UNIQUE INDEX transaction_id_index
    ON transaction (id);

CREATE UNIQUE INDEX filter_id_index
    ON filter (id);

CREATE UNIQUE INDEX idx_id_and_user_id_account
    ON account (id, user_id);

CREATE UNIQUE INDEX idx_id_and_user_id_category
    ON category (id, user_id);

CREATE UNIQUE INDEX idx_id_and_user_id_transaction
    ON transaction (id, user_id);

CREATE UNIQUE INDEX idx_id_and_user_id_filter
    ON filter (id, user_id);



