ALTER TABLE history_entry DROP entry;

ALTER TABLE history_entry
  ADD type VARCHAR(100);

ALTER TABLE history_entry
    ADD object VARCHAR(100);

CREATE TABLE history_info
(
  id            BIGSERIAL PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  old_value     VARCHAR(100) ,
  new_value     VARCHAR(100)
);

CREATE TABLE history_entry_entries
(
  history_entry_id    BIGINT REFERENCES history_entry (id),
  entries_id          BIGINT REFERENCES history_info (id)
);
