package com.pfm.database.row_mappers;

import com.pfm.currency.Currency;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CurrencyRowResultMapper implements RowMapper<Currency> {

  @Override
  public Currency mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
    return Currency.builder()
        .name(resultSet.getString("currency.name"))
        .exchangeRate(resultSet.getBigDecimal("currency.exchange_rate"))
        .build();
  }
}
