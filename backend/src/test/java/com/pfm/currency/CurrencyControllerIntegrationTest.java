package com.pfm.currency;

import static com.pfm.helpers.TestUsersProvider.userMarian;

import com.pfm.helpers.IntegrationTestsBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CurrencyControllerIntegrationTest extends IntegrationTestsBase {

  private static Object[] emptyAccountNameParameters() {
    return new Object[]{"", " ", "    ", null};
  }

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddCategory() throws Exception {
    // when
    callRestToGetAllCurrencies(token);

    // then
    // TODO add assertions

  }

}
