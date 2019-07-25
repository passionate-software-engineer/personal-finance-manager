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
public class TransactionQueryResultMapper implements RowMapper{

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
