package com.pfm.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
@AllArgsConstructor
public class HistoryQueryResultMapper implements RowMapper {

  @Override
  public HistoryQueryResult mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return HistoryQueryResult.builder()
        .date(resultSet.getString("date"))
        .type(resultSet.getString("type"))
        .object(resultSet.getString("object"))
        .name(resultSet.getString("name"))
        .old_value(resultSet.getString("old_value"))
        .new_value(resultSet.getString("new_value"))
        .build();
  }
}
