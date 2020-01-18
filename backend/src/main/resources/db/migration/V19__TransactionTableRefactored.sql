ALTER TABLE transaction
  DROP COLUMN is_recurrent;

UPDATE transaction
  SET recurrence_period = 'NONE'
  WHERE transaction.recurrence_period IS NULL;

ALTER TABLE transaction
  ALTER COLUMN recurrence_period SET DEFAULT 'NONE';