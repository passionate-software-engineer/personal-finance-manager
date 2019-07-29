package com.pfm.database.row.mappers;

import com.pfm.database.query.result.AccountQueryResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountQueryResultMapper implements RowMapper<AccountQueryResult> {

  @Override
  public AccountQueryResult mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
    return AccountQueryResult.builder()
        .name(resultSet.getString("name"))
        .balance(resultSet.getString("balance"))
        .currency(resultSet.getString("currency"))
        .lastVerificationDate(resultSet.getString("last_Verification_Date"))
        .archived(resultSet.getBoolean("archived"))
        .build();
  }
}
