package db.migration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.Application;
import com.pfm.account.AccountRepository;
import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeRepository;
import com.pfm.auth.UserRepository;
import com.pfm.category.CategoryRepository;
import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyRepository;
import java.math.BigDecimal;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class MigrationsTest {

  @Autowired
  protected Flyway flyway;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CurrencyRepository currencyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AccountTypeRepository accountTypeRepository;

  @Test
  public void shouldExecuteAllMigrationsWithSuccess() {
    // TODO add check if correct account types are added and accounts has correct default account type

    // given
    flyway.clean();

    // when
    flyway.migrate();

    // then
    assertCategoriesWereConvertedToFlatStructure();
    assertCurrenciesWereAddedForUsers();
    assertDefaultCurrenciesForAccount();
    assertAccountTypesWereAddedForUsers();
    assertDefaultAccountTypesForAccount();
  }

  private void assertCategoriesWereConvertedToFlatStructure() {
    assertThat(categoryRepository.findById(1L).orElseThrow().getParentCategory(), nullValue());
    assertThat(categoryRepository.findById(2L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(3L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(4L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(5L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(6L).orElseThrow().getParentCategory(), nullValue());
    assertThat(categoryRepository.findById(7L).orElseThrow().getParentCategory().getId(), is(6L));
    assertThat(categoryRepository.findById(8L).orElseThrow().getParentCategory(), nullValue());
    assertThat(categoryRepository.findById(9L).orElseThrow().getParentCategory().getId(), is(8L));
    assertThat(categoryRepository.findById(10L).orElseThrow().getParentCategory().getId(), is(8L));
    assertThat(categoryRepository.findById(11L).orElseThrow().getParentCategory().getId(), is(8L));
    assertThat(categoryRepository.findById(12L).orElseThrow().getParentCategory().getId(), is(8L));
  }

  private void assertCurrenciesWereAddedForUsers() {
    userRepository.findAll().forEach(user -> {
      List<Currency> currencies = currencyRepository.findByUserId(user.getId());
      assertThat(currencies.size(), is(4));
      assertThat(currencies.get(0).getName(), is("PLN"));
      assertThat(currencies.get(1).getName(), is("USD"));
      assertThat(currencies.get(2).getName(), is("EUR"));
      assertThat(currencies.get(3).getName(), is("GBP"));
      assertThat(currencies.get(0).getExchangeRate(), is(BigDecimal.valueOf(100, 2)));
      assertThat(currencies.get(1).getExchangeRate(), is(BigDecimal.valueOf(358, 2)));
      assertThat(currencies.get(2).getExchangeRate(), is(BigDecimal.valueOf(424, 2)));
      assertThat(currencies.get(3).getExchangeRate(), is(BigDecimal.valueOf(499, 2)));
    });
  }

  private void assertDefaultCurrenciesForAccount() {
    accountRepository.findAll().forEach(account -> {
      assertThat(account.getCurrency().getName(), is("PLN"));
    });
  }

  private void assertAccountTypesWereAddedForUsers() {
    userRepository.findAll().forEach(user -> {
      List<AccountType> types = accountTypeRepository.findByUserId(user.getId());
      assertThat(types.size(), is(4));
      assertThat(types.get(0).getName(), is("Personal"));
      assertThat(types.get(1).getName(), is("Investment"));
      assertThat(types.get(2).getName(), is("Saving"));
      assertThat(types.get(3).getName(), is("Credit"));
    });
  }

  private void assertDefaultAccountTypesForAccount() {
    accountRepository.findAll().forEach(account -> {
      assertThat(account.getType().getName(), is("Personal"));
    });

  }
}
