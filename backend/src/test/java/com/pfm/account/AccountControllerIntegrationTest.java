package com.pfm.account;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class AccountControllerIntegrationTest extends IntegrationTestsBase {

  @Before
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddAccount() throws Exception {

    //given
    Account account = accountJacekBalance1000();

    //when
    String respone =
        mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(convertAccountToAccountRequest(account))))
            .andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString();

    //then
    Long accountId = Long.parseLong(respone);

    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(accountId.intValue())))
        .andExpect(jsonPath("$.name", is(account.getName())))
        .andExpect(jsonPath("$.balance", is(account.getBalance().toString())))
        .andExpect(jsonPath("$.userId").doesNotExist());

  }

  @Test
  @Parameters(method = "emptyAccountNameParameters")
  public void shouldReturnErrorCausedByEmptyNameField(String name, BigDecimal balance) throws Exception {

    //given
    AccountRequest accountRequest = AccountRequest.builder().name(name).balance(balance).build();

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

  @SuppressWarnings("unused")
  private Collection<Object[]> emptyAccountNameParameters() {
    return Arrays.asList(new Object[][]{
        {"", null},
        {" ", null},
        {"    ", null},
        {null, null}
    });
  }

  @Test
  public void shouldGetAccountById() throws Exception {

    //given
    Account account = accountMbankBalance10();
    Long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print())
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
    Account accountMbank = accountMbankBalance10();

    Long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacek, token);
    Long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
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
    Long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    Account updatedAccount = accountMbankBalance10();

    //when
    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(updatedAccount))))
        .andDo(print())
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
  public void shouldUpdateAccountWithUpdatedAccountSameNameAsBefore() throws Exception {

    //given
    Long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);
    AccountRequest updatedAccount = AccountRequest.builder()
        .name(accountMbankBalance10().getName())
        .balance(convertDoubleToBigDecimal(666)).build();

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andDo(print())
        .andExpect(status().isOk());

    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(accountId.intValue())))
        .andExpect(jsonPath("$.name", is(equalTo(updatedAccount.getName()))))
        .andExpect(jsonPath("$.balance", is(equalTo(updatedAccount.getBalance().toString()))))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldReturnErrorCauseByDuplicatedNameWhileUpdatingAccount() throws Exception {

    //given
    callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);
    long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);

    AccountRequest updatedAccount = AccountRequest.builder()
        .name(accountMbankBalance10().getName())
        .balance(convertDoubleToBigDecimal(432))
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
    long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    AccountRequest accountToUpdate = AccountRequest.builder()
        .name("")
        .balance(account.getBalance())
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
    long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);

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
    callRestServiceToAddAccountAndReturnId(account, token);
    AccountRequest accountRequestToAdd = AccountRequest.builder()
        .name(account.getName())
        .balance(convertDoubleToBigDecimal(100))
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