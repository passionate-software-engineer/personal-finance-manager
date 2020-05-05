package com.pfm.export.converter;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.export.ExportResult.ExportAccount;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountExporter {

  private final AccountService accountService;

  static ExportAccount mapToExportAccount(final Account account) {
    return ExportAccount.builder()
        .name(account.getName())
        .balance(account.getBalance())
        .currency(account.getCurrency().getName())
        .accountType(account.getType().getName())
        .lastVerificationDate(account.getLastVerificationDate())
        .archived(account.isArchived())
        .build();
  }

  public List<ExportAccount> export(long userId) {
    return accountService.getAccounts(userId).stream()
        .map(AccountExporter::mapToExportAccount)
        .collect(Collectors.toList());
  }

}
