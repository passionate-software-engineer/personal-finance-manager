package com.pfm.database.row.mappers;

import com.pfm.database.query.result.CurrencyQueryResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CurrencyQueryResultMapper implements RowMapper<CurrencyQueryResult> {

  @Override
  public CurrencyQueryResult mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return CurrencyQueryResult.builder()
        .name(resultSet.getString("name"))
        .exchangeRate(resultSet.getString("exchange_rate"))
        .build();
  }
}
