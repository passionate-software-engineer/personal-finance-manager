UPDATE account
 SET archived = FALSE

-- Added updates of value, due to the NullPointerException, because this value does not exist,
-- the change in value is carried out in tests, because its modification in the main migration is no longer possible

