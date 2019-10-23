ALTER TABLE transaction
  ADD is_planned BOOLEAN DEFAULT FALSE;

ALTER TABLE transaction
  ADD recurrence_period varchar(255);

-- CREATE TABLE reccurence_period
-- (
--   id          BIGSERIAL PRIMARY KEY,
--   name        VARCHAR(255) NOT NULL,
--   description VARCHAR(255),
--   price_from  NUMERIC(19, 2),
--   price_to    NUMERIC(19, 2),
--   date_from   DATE,
--   date_to     DATE
-- );
