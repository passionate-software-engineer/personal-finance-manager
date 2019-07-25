package com.pfm.database;

import java.math.BigDecimal;
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

  private BigDecimal balance;

  private long user_id;

  private String lastVerificationDate;
  
  private boolean archived;

}
