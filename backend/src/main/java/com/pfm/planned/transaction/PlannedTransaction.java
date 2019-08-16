package com.pfm.planned.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.WithTransactionProperties;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
public class PlannedTransaction implements WithTransactionProperties {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String description;

  private Long categoryId;

  private LocalDate date;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<AccountPriceEntry> accountPriceEntries;

  @JsonIgnore
  private Long userId;

  @Builder
  public PlannedTransaction(Long id, String description, Long categoryId, LocalDate date,
      List<AccountPriceEntry> accountPriceEntries, Long userId) {
    this.id = id;
    this.description = description;
    this.accountPriceEntries = accountPriceEntries;
    this.categoryId = categoryId;

    this.date = date;
  }

}
