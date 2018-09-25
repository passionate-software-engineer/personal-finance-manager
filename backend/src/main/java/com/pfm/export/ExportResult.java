package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.category.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResult {

  private List<Account> initialAccountsState;
  private List<Account> finalAccountsState;

  private List<ExportPeriod> periods;
  private List<Category> categories;

}
