CREATE TABLE user
(
  id          BIGSERIAL PRIMARY KEY,
  username    VARCHAR(255) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  first_name  VARCHAR(255) NOT NULL,
  last_name   VARCHAR(255) NOT NULL
);


