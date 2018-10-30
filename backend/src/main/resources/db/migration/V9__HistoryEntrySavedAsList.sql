ALTER TABLE history_entry DROP entry;

CREATE TABLE history_entry_entry
(
  history_entry_id  BIGINT,
  entry VARCHAR(255) NOT NULL
);