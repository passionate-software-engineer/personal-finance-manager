CREATE TABLE history_entry
(
  id      BIGSERIAL PRIMARY KEY,
  entry   VARCHAR(355) NOT NULL,
  date    TIMESTAMP    NOT NULL,
  user_id BIGINT REFERENCES app_user (id)
);