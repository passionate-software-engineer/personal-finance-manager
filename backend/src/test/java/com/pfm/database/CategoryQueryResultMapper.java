package com.pfm.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CategoryQueryResultMapper implements RowMapper<CategoryQueryResult> {

  @Override
  public CategoryQueryResult mapRow(ResultSet resultSet, int rowNumber) throws SQLException {

    return CategoryQueryResult.builder()
        .name(resultSet.getString("name"))
        .parentCategory(resultSet.getString("parent_category"))
        .build();
  }
}
