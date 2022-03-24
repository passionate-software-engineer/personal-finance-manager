ALTER TABLE transaction
    ADD is_recurrent BOOLEAN DEFAULT FALSE;

ALTER TABLE transaction
    ADD recurrence_period integer;
