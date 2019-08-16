package com.pfm.transaction;

import java.time.LocalDate;
import java.util.List;

public interface WithTransactionProperties {

  Long getId();

  String getDescription();

  Long getCategoryId();

  LocalDate getDate();

  List<AccountPriceEntry> getAccountPriceEntries();

  Long getUserId();

}
