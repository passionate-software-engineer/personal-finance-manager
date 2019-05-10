package com.pfm.currency;

import static com.pfm.config.MessagesProvider.ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock
  private CurrencyRepository currencyRepository;

  @InjectMocks
  private CurrencyService currencyService;

  @Test
  public void shouldReturnErrorWhenCurrencyDoNotExists() {
    // given
    int currencyId = 14;
    int userId = 10;

    when(currencyRepository.findByIdAndUserId(currencyId, userId)).thenReturn(Optional.empty());

    // when

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      currencyService.getCurrencyByIdAndUserId(currencyId, userId);
    });

    //then
    assertThat(exception.getMessage(), is(equalTo(String.format(getMessage(ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST), currencyId))));
  }

}
