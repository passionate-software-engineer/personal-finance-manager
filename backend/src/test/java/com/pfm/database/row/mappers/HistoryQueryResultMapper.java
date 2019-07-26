package com.pfm.database.row.mappers;

import com.pfm.database.HistoryQueryResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class HistoryQueryResultMapper implements RowMapper<HistoryQueryResult> {

  @Override
  public HistoryQueryResult mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return HistoryQueryResult.builder()
        .date(resultSet.getString("date"))
        .type(resultSet.getString("type"))
        .object(resultSet.getString("object"))
        .name(resultSet.getString("name"))
        .oldValue(resultSet.getString("old_value"))
        .newValue(resultSet.getString("new_value"))
        .build();
  }
}
