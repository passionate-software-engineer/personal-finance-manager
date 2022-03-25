ALTER TABLE transaction
    ADD recurrence_period_copy integer;

UPDATE transaction
SET recurrence_period_copy = recurrence_period;

ALTER TABLE transaction
    ALTER COLUMN recurrence_period
        SET DATA TYPE VARCHAR(32);

UPDATE transaction
SET RECURRENCE_PERIOD = 'EVERY_MONTH'
WHERE transaction.recurrence_period_copy = 3;

UPDATE transaction
SET RECURRENCE_PERIOD = 'EVERY_WEEK'
WHERE transaction.recurrence_period_copy = 2;

UPDATE transaction
SET RECURRENCE_PERIOD = 'EVERY_DAY'
WHERE transaction.recurrence_period_copy = 1;

UPDATE transaction
SET RECURRENCE_PERIOD = 'NONE'
WHERE transaction.recurrence_period_copy = 0;

ALTER TABLE transaction
DROP
COLUMN recurrence_period_copy;
