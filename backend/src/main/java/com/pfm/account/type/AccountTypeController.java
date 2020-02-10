package com.pfm.account.type;

import com.pfm.auth.UserProvider;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AccountTypeController implements AccountTypeApi {

  private AccountTypeService accountTypeService;
  private UserProvider userProvider;

  @Override
  public ResponseEntity<List<AccountType>> getAccountType() {
    long userId = userProvider.getCurrentUserId();

    log.info("Returning list of currencies for user " + userId);

    List<AccountType> accountType = accountTypeService.getAccountType(userId);

    return ResponseEntity.ok(accountType);
  }
}
