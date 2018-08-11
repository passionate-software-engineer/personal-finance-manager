package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pfm.account.Account;
import com.pfm.category.Category;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

  private int id;
  private LocalDate date;
  private String description;
  private Category category;
  private Account account;
  private BigDecimal price;
}
