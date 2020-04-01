ALTER TABLE category
  ADD priority integer default 1000 CHECK (priority > 0 AND priority <= 1000);
