alter table TRANSACTION
    alter COLUMN recurrence_period
        SET DATA TYPE VARCHAR(32);

update TRANSACTION
set RECURRENCE_PERIOD = 'EVERY_MONTH'
where TRANSACTION.recurrence_period_copy = 3;

update TRANSACTION
set RECURRENCE_PERIOD = 'EVERY_WEEK'
where TRANSACTION.recurrence_period_copy = 2;

update TRANSACTION
set RECURRENCE_PERIOD = 'EVERY_DAY'
where TRANSACTION.recurrence_period_copy = 1;

update TRANSACTION
set RECURRENCE_PERIOD = 'NONE'
where TRANSACTION.recurrence_period_copy = 0;

alter table transaction
drop COLUMN recurrence_period_copy;