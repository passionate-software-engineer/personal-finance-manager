CREATE TABLE app_user
(
  id         BIGSERIAL PRIMARY KEY,
  username   VARCHAR(600) NOT NULL UNIQUE,
  password   VARCHAR(600) NOT NULL,
  first_name VARCHAR(600) NOT NULL,
  last_name  VARCHAR(600) NOT NULL
);g


