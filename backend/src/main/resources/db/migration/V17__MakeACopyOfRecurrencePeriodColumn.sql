alter table TRANSACTION
  add recurrence_period_copy integer;
   update TRANSACTION set recurrence_period_copy = recurrence_period ;
