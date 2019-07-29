package com.pfm.database.row.mappers;

import com.pfm.database.query.result.TransactionQueryResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TransactionQueryResultMapper implements RowMapper<TransactionQueryResult> {

  @Override
  public TransactionQueryResult mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
    return TransactionQueryResult.builder()
        .date(resultSet.getString("date"))
        .description(resultSet.getString("description"))
        .price(resultSet.getString("price"))
        .account(resultSet.getString("account"))
        .category(resultSet.getString("category"))
        .build();
  }
}
