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
public class CategoryFromMainParentCategoryQueryResultMapper implements RowMapper {

  @Override
  public CategoryFromMainParentCategoryQueryResult mapRow(ResultSet resultSet, int rowNumber) throws SQLException {

    return CategoryFromMainParentCategoryQueryResult.builder()
        .name(resultSet.getString("name"))
        .build();
  }
}
