package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;

public class AccountControllerIntegrationTest extends IntegrationTestsBase {

  public static final String MARK_AS_ARCHIVED = "/markAsArchived";
  public static final String MARK_AS_ACTIVE = "/markAsActive";

  private static Collection<Object[]> emptyAccountNameParameters() {
    return Arrays.asList(new Object[][]{
        {"", null},
        {" ", null},
        {"    ", null},
        {null, null}
    });
  }

  @BeforeEach
  public void setUp() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddAccount() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    //when
    String response =
        mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(convertAccountToAccountRequest(account))))
            .andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString();

    //then
    Long accountId = Long.parseLong(response);

    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(accountId.intValue())))
        .andExpect(jsonPath("$.name", is(account.getName())))
        .andExpect(jsonPath("$.balance", is(account.getBalance().toString())))
        .andExpect(jsonPath("$.userId").doesNotExist());

  }

  @ParameterizedTest
  @MethodSource("emptyAccountNameParameters")
  public void shouldReturnErrorCausedByEmptyNameField(String name, BigDecimal balance) throws Exception {
    //given
    AccountRequest accountRequest = AccountRequest.builder()
        .name(name)
        .balance(balance)
        .currencyId(currencyService.getCurrencies(userId).get(0).getId())
        .build();

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_NAME))))
        .andExpect(jsonPath("$[1]", is(getMessage(EMPTY_ACCOUNT_BALANCE))));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingCurrencyOnAddAccount() throws Exception {
    //given
    long notExistingCurrencyId = 3124151L;

    AccountRequest accountRequest = AccountRequest.builder()
        .name("mBank")
        .balance(BigDecimal.TEN)
        .currencyId(notExistingCurrencyId)
        .build();

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST), notExistingCurrencyId))));
  }

  @Test
  public void shouldGetAccountById() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    Long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(accountId.intValue())))
        .andExpect(jsonPath("$.name", is(account.getName())))
        .andExpect(jsonPath("$.balance", is(account.getBalance().toString())))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingId() throws Exception {
    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldGetAllAccounts() throws Exception {
    //given
    Account accountJacek = accountJacekBalance1000();
    accountJacek.setCurrency(currencyService.getCurrencies(userId).get(0));

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(0));

    Long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacek, token);
    Long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(accountJacekId.intValue())))
        .andExpect(jsonPath("$[0].name", is(accountJacek.getName())))
        .andExpect(jsonPath("$[0].balance", is(accountJacek.getBalance().toString())))
        .andExpect(jsonPath("$[0].userId").doesNotExist())
        .andExpect(jsonPath("$[1].id", is(accountMbankId.intValue())))
        .andExpect(jsonPath("$[1].name", is(accountMbank.getName())))
        .andExpect(jsonPath("$[1].balance", is(accountMbank.getBalance().toString())))
        .andExpect(jsonPath("$[1].userId").doesNotExist());
  }

  @Test
  public void shouldUpdateAccount() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    Long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    Account updatedAccount = accountMbankBalance10();
    updatedAccount.setCurrency(currencyService.getCurrencies(userId).get(1));

    //when
    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(updatedAccount))))
        .andExpect(status().isOk());

    //then
    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(accountId.intValue())))
        .andExpect(jsonPath("$.name", is(updatedAccount.getName())))
        .andExpect(jsonPath("$.balance", is(updatedAccount.getBalance().toString())))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldSetAccountAsArchived() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.archived", is(false)));

    //when
    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + MARK_AS_ARCHIVED)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    //then
    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is((int) accountId)))
        .andExpect(jsonPath("$.name", is(account.getName())))
        .andExpect(jsonPath("$.balance", is(account.getBalance().toString())))
        .andExpect(jsonPath("$.archived", is(true)))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldSetAccountAsActive() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + MARK_AS_ARCHIVED)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    //when
    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + MARK_AS_ACTIVE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    //then
    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is((int) accountId)))
        .andExpect(jsonPath("$.name", is(account.getName())))
        .andExpect(jsonPath("$.balance", is(account.getBalance().toString())))
        .andExpect(jsonPath("$.archived", is(false)))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldReturnAccountNotFoundWhenTryingToMakeActiveNotExistingAccount() throws Exception {
    //given
    int accountId = 2500;

    //when
    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + MARK_AS_ACTIVE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        // then
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateAccountLastVerificationDate() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    //when
    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + "/markAccountAsVerifiedToday")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    //then
    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is((int) accountId)))
        .andExpect(jsonPath("$.name", is(account.getName())))
        .andExpect(jsonPath("$.balance", is(account.getBalance().toString())))
        .andExpect(jsonPath("$.lastVerificationDate", is(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldReturnAccountNotFoundWhenTryingToUpdateNotExistingAccountLastVerificationDate() throws Exception {
    //given
    int accountId = 1500;

    //when
    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + "/markAccountAsVerifiedToday")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        // then
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnAccountNotFoundWhenTryingToArchiveNotExistingAccount() throws Exception {
    //given
    int accountId = 1500;

    //when
    mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + MARK_AS_ARCHIVED)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        // then
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateAccountWithUpdatedAccountSameNameAsBefore() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    Long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    AccountRequest updatedAccount = AccountRequest.builder()
        .name(account.getName())
        .balance(convertDoubleToBigDecimal(666))
        .currencyId(account.getCurrency().getId())
        .build();

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andExpect(status().isOk());

    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(accountId.intValue())))
        .andExpect(jsonPath("$.name", is(equalTo(updatedAccount.getName()))))
        .andExpect(jsonPath("$.balance", is(equalTo(updatedAccount.getBalance().toString()))))
        .andExpect(jsonPath("$.currency.name", is(equalTo(account.getCurrency().getName()))))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldReturnErrorCauseByDuplicatedNameWhileUpdatingAccount() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    callRestServiceToAddAccountAndReturnId(account, token);

    Account jacekAccount = accountJacekBalance1000();
    jacekAccount.setCurrency(currencyService.getCurrencies(userId).get(1));
    long accountJacekId = callRestServiceToAddAccountAndReturnId(jacekAccount, token);

    AccountRequest updatedAccount = AccountRequest.builder()
        .name(account.getName())
        .balance(convertDoubleToBigDecimal(432))
        .currencyId(account.getCurrency().getId())
        .build();

    //when
    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountJacekId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]",
            is(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingCurrencyOnUpdateAccount() throws Exception {
    //given
    long notExistingCurrencyId = 3124151L;

    Account jacekAccount = accountJacekBalance1000();
    jacekAccount.setCurrency(currencyService.getCurrencies(userId).get(1));
    long accountJacekId = callRestServiceToAddAccountAndReturnId(jacekAccount, token);

    AccountRequest updatedAccount = AccountRequest.builder()
        .name(jacekAccount.getName())
        .balance(convertDoubleToBigDecimal(4322))
        .currencyId(notExistingCurrencyId)
        .build();

    //when
    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountJacekId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST), notExistingCurrencyId))));
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInUpdateMethod() throws Exception {
    //when
    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(convertAccountToAccountRequest(accountMbankBalance10()))))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCauseByNotValidAccountUpdateMethod() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    AccountRequest accountToUpdate = AccountRequest.builder()
        .name("")
        .balance(account.getBalance())
        .currencyId(account.getCurrency().getId())
        .build();

    //when
    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountToUpdate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_NAME))));
  }

  @Test
  public void shouldDeleteAccount() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    //when
    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInDeleteMethod() throws Exception {
    //when
    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByExistingAccountName() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    callRestServiceToAddAccountAndReturnId(account, token);
    AccountRequest accountRequestToAdd = AccountRequest.builder()
        .name(account.getName())
        .balance(convertDoubleToBigDecimal(100))
        .currencyId(account.getCurrency().getId())
        .build();

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountRequestToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

}
