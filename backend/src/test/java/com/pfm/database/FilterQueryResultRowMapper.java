package com.pfm.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FilterQueryResultRowMapper implements RowMapper {

  @Override
  public FilterQueryResult mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return FilterQueryResult.builder()
        .name(resultSet.getString("name"))
        .description(resultSet.getString("description"))
        .priceFrom(resultSet.getString("price_from"))
        .priceTo(resultSet.getString("price_to"))
        .dateFrom(resultSet.getString("date_from"))
        .dateTo(resultSet.getString("date_to"))
        .priceTo(resultSet.getString("price_to"))
        .build();
  }
}
