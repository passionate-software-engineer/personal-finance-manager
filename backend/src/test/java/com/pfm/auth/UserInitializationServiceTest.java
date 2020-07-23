package com.pfm.auth;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.pfm.account.type.AccountTypeService;
import com.pfm.category.CategoryService;
import com.pfm.currency.CurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserInitializationServiceTest {

  private static final long MOCK_USER_ID = 999;

  @Mock
  private CategoryService categoryService;

  @Mock
  private CurrencyService currencyService;

  @Mock
  private AccountTypeService accountTypeService;

  @InjectMocks
  private UserInitializationService userInitializationService;

  @Test
  void shouldCorrectlyInitializeUser() {
    // given
    doNothing().when(currencyService).addDefaultCurrencies(anyLong());
    doNothing().when(accountTypeService).addDefaultAccountTypes(anyLong());

    // when
    userInitializationService.initializeUser(MOCK_USER_ID);

    // then
    verify(currencyService, times(1)).addDefaultCurrencies(anyLong());
    verify(accountTypeService, times(1)).addDefaultAccountTypes(anyLong());
    verify(categoryService, times(1)).addDefaultCategories(eq(MOCK_USER_ID));
  }
}
