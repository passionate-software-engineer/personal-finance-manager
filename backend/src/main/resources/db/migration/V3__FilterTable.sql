CREATE TABLE filter
(
  id           SERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(255) NOT NULL,
  priceFrom    NUMERIC(19, 2),
  priceTo      NUMERIC(19, 2),
  dateFrom     DATE,
  dateTo       DATE
);