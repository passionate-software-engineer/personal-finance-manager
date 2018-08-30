package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_LUKASZ_BALANCE_1124;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MARCIN_BALANCE_10_99;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_RAFAL_BALANCE_0;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
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

import com.pfm.IntegrationTestsBase;
import com.pfm.account.AccountRequest.AccountRequestBuilder;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import junitparams.Parameters;
import org.junit.Test;

public class AccountControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldAddAccount() throws Exception {

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(accountJacekBalance1000()))))
        .andExpect(status().isOk());

    //then
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + 1))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Jacek Millenium Bank savings")))
        .andExpect(jsonPath("$.balance", is("1000.00")));

  }

  @Test
  @Parameters(method = "emptyAccountNameParameters")
  public void shouldReturnErrorCausedByEmptyNameField(String name, BigDecimal balance) throws Exception {

    //given
    AccountRequest accountRequest = AccountRequest.builder().name(name).balance(balance).build();

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
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
    long accountId = callRestServiceToAddAccountAndReturnId(ACCOUNT_LUKASZ_BALANCE_1124);

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Lukasz CreditBank")))
        .andExpect(jsonPath("$.balance", is("1124.00")));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingId() throws Exception {

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldGetAllAccounts() throws Exception {

    //given
    callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    callRestServiceToAddAccountAndReturnId(accountMbankBalance10());

    //when
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Jacek Millenium Bank savings")))
        .andExpect(jsonPath("$[0].balance", is("1000.00")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].name", is("Mbank")))
        .andExpect(jsonPath("$[1].balance", is("10.00")));
  }

  @Test
  public void shouldUpdateAccount() throws Exception {

    //given
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());

    //when
    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(accountMbankBalance10()))))
        .andDo(print())
        .andExpect(status().isOk());

    //then
    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is(equalTo("Mbank"))))
        .andExpect(jsonPath("$.balance", is(equalTo("10.00"))));
  }

  @Test
  public void shouldUpdateAccountWithUpdatedAccountSameNameAsBefore() throws Exception {

    //given
    long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10());
    AccountRequest updatedAccount = AccountRequest.builder()
        .name(accountMbankBalance10().getName())
        .balance(convertDoubleToBigDecimal(666)).build();

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andDo(print())
        .andExpect(status().isOk());

    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is(equalTo("Mbank"))))
        .andExpect(jsonPath("$.balance", is(equalTo("666.00"))));
  }

  @Test
  public void shouldReturnErrorCauseByDuplicatedNameWhileUpdatingAccount() throws Exception {

    //given
    callRestServiceToAddAccountAndReturnId(accountMbankBalance10());
    long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());

    AccountRequest updatedAccount = AccountRequest.builder()
        .name(accountMbankBalance10().getName())
        .balance(convertDoubleToBigDecimal(432))
        .build();

    //when
    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountJacekId)
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
            .contentType(JSON_CONTENT_TYPE)
            .content(json(convertAccountToAccountRequest(accountMbankBalance10()))))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCauseByNotValidAccountUpdateMethod() throws Exception {

    //given
    long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10());
    AccountRequest accountToUpdate = AccountRequest.builder()
        .name("")
        .balance(convertAccountToAccountRequest(accountMbankBalance10()).getBalance())
        .build();

    //when
    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountToUpdate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_NAME))));
  }

  @Test
  public void shouldDeleteAccount() throws Exception {

    //given
    long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10());

    //when
    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInDeleteMethod() throws Exception {

    //when
    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByExistingAccountName() throws Exception {

    //given
    callRestServiceToAddAccountAndReturnId(accountMbankBalance10());
    AccountRequest accountRequestToAdd = AccountRequest.builder()
        .name(accountMbankBalance10().getName())
        .balance(convertDoubleToBigDecimal(100))
        .build();

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountRequestToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

}