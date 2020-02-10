package com.pfm.account.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AccountTypeService {

  private AccountTypeRepository accountTypeRepository;

  public Optional<AccountType> findAccountTypeByIdAndUserId(long accountTypeId, long userId) {
    return accountTypeRepository.findByIdAndUserId(accountTypeId, userId);
  }

  public AccountType getAccountTypeByIdAndUserId(long accountTypeId, long userId) {
    Optional<AccountType> accountTypeOptional = accountTypeRepository.findByIdAndUserId(accountTypeId, userId);
    if (!accountTypeOptional.isPresent()) {
      throw new IllegalStateException(String.format(getMessage(ACCOUNT_TYPE_ID_DOES_NOT_EXIST), accountTypeId));
    }
    return accountTypeOptional.get();
  }

  public List<AccountType> getAccountType(long userId) {
    return accountTypeRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(AccountType::getName))
        .collect(Collectors.toList());
  }

  public void addDefaultAccountTypes(long userId) {
    accountTypeRepository.save(AccountType.builder().name("Personal").userId(userId).build());
    accountTypeRepository.save(AccountType.builder().name("Investment").userId(userId).build());
    accountTypeRepository.save(AccountType.builder().name("Saving").userId(userId).build());

  }

}
