ALTER TABLE transaction
  ADD is_planned BOOLEAN DEFAULT FALSE;

ALTER TABLE transaction
  ADD recurrence_period integer;
