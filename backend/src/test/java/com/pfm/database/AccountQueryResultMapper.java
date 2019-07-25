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
//@NoArgsConstructor
public class AccountQueryResultMapper implements RowMapper {

  @Override
  public AccountQueryResult mapRow(ResultSet resultSet, int rowNumber) throws SQLException {

    return AccountQueryResult.builder()
        .name(resultSet.getString("name"))
        .balance(resultSet.getBigDecimal("balance"))
        .user_id(resultSet.getLong("user_id"))
        .lastVerificationDate(resultSet.getString("last_Verification_Date") == null ? "null" : "not null")
        .archived(resultSet.getBoolean("archived"))
        .build();

  }

}
