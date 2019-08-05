package com.pfm.database.query.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionQueryResult {

  private String date;

  private String description;

  private String price;

  private String account;

  private String category;

}
