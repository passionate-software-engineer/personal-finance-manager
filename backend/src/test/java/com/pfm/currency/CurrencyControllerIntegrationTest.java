package com.pfm.currency;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CurrencyControllerIntegrationTest extends IntegrationTestsBase {

  @BeforeEach
  public void beforeEach() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddCategory() throws Exception {
    // when
    List<Currency> currencies = callRestToGetAllCurrencies(token);

    // then
    assertThat(currencies, is(notNullValue()));
    assertThat(currencies.size(), is(4));

    assertThat(currencies.get(0).getName(), is("EUR"));
    assertThat(currencies.get(0).getExchangeRate(), is(new BigDecimal("4.24")));

    assertThat(currencies.get(1).getName(), is("GBP"));
    assertThat(currencies.get(1).getExchangeRate(), is(new BigDecimal("4.99")));

    assertThat(currencies.get(2).getName(), is("PLN"));
    assertThat(currencies.get(2).getExchangeRate(), is(new BigDecimal("1.00")));

    assertThat(currencies.get(3).getName(), is("USD"));
    assertThat(currencies.get(3).getExchangeRate(), is(new BigDecimal("3.58")));
  }

}
