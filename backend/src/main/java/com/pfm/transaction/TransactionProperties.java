package com.pfm.transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionProperties {

  public Long getId();

  public String getDescription();

  public Long getCategoryId();

  public LocalDate getDate();

  public List<AccountPriceEntry> getAccountPriceEntries();

  public Long getUserId();
}
