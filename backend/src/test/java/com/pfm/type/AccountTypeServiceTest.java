package com.pfm.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.account.type.AccountTypeRepository;
import com.pfm.account.type.AccountTypeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTypeServiceTest {

  @Mock
  private AccountTypeRepository accountTypeRepository;

  @InjectMocks
  private AccountTypeService accountTypeService;

  @Test
  public void shouldReturnErrorWhenAccountTypeDoNotExists() {
    // given
    int accountTypeId = 14;
    int userId = 10;

    when(accountTypeRepository.findByIdAndUserId(accountTypeId, userId)).thenReturn(Optional.empty());

    // when

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      accountTypeService.getAccountTypeByIdAndUserId(accountTypeId, userId);
    });

    //then
    assertThat(exception.getMessage(), is(equalTo(String.format(getMessage(ACCOUNT_TYPE_ID_DOES_NOT_EXIST), accountTypeId))));
  }

}
