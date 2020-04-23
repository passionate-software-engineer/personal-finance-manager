package com.pfm.export.converter;

import static org.mockito.Mockito.when;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.account.type.AccountType;
import com.pfm.currency.Currency;
import com.pfm.export.ExportResult.ExportAccount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountExporterTest {

  private static final long USER_ID = 1L;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountExporter accountExporter;

  @Test
  public void shouldConvertAccountServiceToAccountExport() {
    // given
    Account account = createAccount();
    when(accountService.getAccounts(USER_ID)).thenReturn(List.of(account));

    // when
    List<ExportAccount> export = accountExporter.export(USER_ID);

    // then
    Assertions.assertEquals(export.size(), 1);
    ExportAccount exportAccount = export.get(0);
    Assertions.assertEquals(exportAccount.getAccountType(), account.getType().getName());
    Assertions.assertEquals(exportAccount.getBalance(), account.getBalance());
    Assertions.assertEquals(exportAccount.getCurrency(), account.getCurrency().getName());
    Assertions.assertEquals(exportAccount.getLastVerificationDate(), account.getLastVerificationDate());
  }

  private Account createAccount() {
    return Account.builder()
        .balance(BigDecimal.TEN)
        .currency(Currency.builder().name("USD").build())
        .lastVerificationDate(LocalDate.now())
        .type(AccountType.builder().name("ACCOUNT_TYPE").build())
        .build();
  }
}
