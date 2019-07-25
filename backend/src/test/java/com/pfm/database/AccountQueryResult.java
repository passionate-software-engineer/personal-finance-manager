package com.pfm.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryResult {

  private String name;

  private String balance;

  private String lastVerificationDate;

  private boolean archived;

}
