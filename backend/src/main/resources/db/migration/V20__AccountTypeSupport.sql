CREATE TABLE account_type
(
  id            BIGSERIAL PRIMARY KEY,
  name          VARCHAR(50)    NOT NULL,
  user_id       BIGINT REFERENCES app_user (id)
);

ALTER TABLE account
  ADD type_id BIGINT REFERENCES account_type (id);
