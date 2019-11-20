ALTER TABLE TRANSACTION
  ADD recurrence_period_copy integer;
   UPDATE TRANSACTION SET recurrence_period_copy = recurrence_period ;
