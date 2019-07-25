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
public class AccountQueryResultMapper implements RowMapper<AccountQueryResult> {

  @Override
  public AccountQueryResult mapRow(ResultSet resultSet, int rowNumber) throws SQLException {

    return AccountQueryResult.builder()
        .name(resultSet.getString("name"))
        .balance(resultSet.getString("balance"))
        .lastVerificationDate(resultSet.getString("last_Verification_Date"))
        .archived(resultSet.getBoolean("archived"))
        .build();
  }

}
