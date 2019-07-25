package com.pfm.database;

import com.pfm.currency.Currency;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CurrencyRowMapper implements RowMapper {

  CurrencyRowMapper() {
  }

  @Override
  public Currency mapRow(ResultSet resultSet, int rowNumber) throws SQLException {

    Currency currency = new Currency();

    currency.setId(resultSet.getLong("currency.id"));

    currency.setName(resultSet.getString("currency.name"));

    currency.setExchangeRate(resultSet.getBigDecimal("currency.exchange_rate"));

    return currency;
  }
}
