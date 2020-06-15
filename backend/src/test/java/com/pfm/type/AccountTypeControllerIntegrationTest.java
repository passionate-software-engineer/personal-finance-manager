package com.pfm.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_TYPE_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountTypeProvider.accountInvestment;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeRequest;
import com.pfm.helpers.IntegrationTestsBase;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;

public class AccountTypeControllerIntegrationTest extends IntegrationTestsBase {

  @BeforeEach
  public void setUp() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldGetAccountTypeById() throws Exception {

    // given
    AccountType accountType = AccountType.builder().name("AccountInvestment").build();

    // when
    String response =
        mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountType)))
            .andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString();

    // then
    Long accountTypeId = Long.parseLong(response);
    mockMvc
        .perform(get(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(accountType.getName())))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldAddAccountType() throws Exception {
    // given
    final List<AccountType> accountTypes = accountTypeService.getAccountTypes(userId);
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    // when
    String response =
        mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountTypeRequest)))
            .andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString();

    // then
    Long accountTypeId = Long.parseLong(response);

    accountTypes.add(AccountType.builder().name(accountTypeRequest.getName()).id(accountTypeId).build());
    accountTypes.sort(Comparator.comparing(AccountType::getName));

    mockMvc
        .perform(get(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0].id", is(accountTypes.get(0).getId().intValue())))
        .andExpect(jsonPath("$[1].id", is(accountTypes.get(1).getId().intValue())))
        .andExpect(jsonPath("$[2].id", is(accountTypes.get(2).getId().intValue())))
        .andExpect(jsonPath("$[3].id", is(accountTypes.get(3).getId().intValue())))
        .andExpect(jsonPath("$[4].id", is(accountTypes.get(4).getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(accountTypes.get(0).getName())))
        .andExpect(jsonPath("$[1].name", is(accountTypes.get(1).getName())))
        .andExpect(jsonPath("$[2].name", is(accountTypes.get(2).getName())))
        .andExpect(jsonPath("$[3].name", is(accountTypes.get(3).getName())))
        .andExpect(jsonPath("$[4].name", is(accountTypes.get(4).getName())))
        .andExpect(jsonPath("$[0].userId").doesNotExist())
        .andExpect(jsonPath("$[1].userId").doesNotExist())
        .andExpect(jsonPath("$[2].userId").doesNotExist())
        .andExpect(jsonPath("$[3].userId").doesNotExist())
        .andExpect(jsonPath("$[4].userId").doesNotExist());
  }

  @ParameterizedTest
  @MethodSource("emptyAccountTypeNameParameters")
  public void shouldThrowErrorIfAccountTypeIsNull(String name) throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name(name).build();

    // when
    mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_TYPE_NAME))));
  }

  @SuppressWarnings("unused")
  private static Collection<Object[]> emptyAccountTypeNameParameters() {
    return Arrays.asList(new Object[][] {
        {""},
        {" "},
        {"    "},
        {null}
    });
  }

  @Test
  public void shouldReturnErrorCausedByExistingAccountTypeName() throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    // when
    mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeRequest)))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();

    // then
    mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInDeleteMethod() throws Exception {

    // when
    mockMvc
        .perform(delete(ACCOUNT_TYPE_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldDeleteAccountType() throws Exception {

    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    long accountTypeId = callRestServiceToAddAccountTypeAndReturnId(accountTypeRequest, token);

    // when
    mockMvc.perform(delete(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCauseByNotValidAccountTypeUpdateMethod() throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    long accountTypeId = callRestServiceToAddAccountTypeAndReturnId(accountTypeRequest, token);
    AccountTypeRequest accountTypeToUpdate = AccountTypeRequest.builder()
        .name("")
        .build();

    // when
    mockMvc
        .perform(put(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountTypeToUpdate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_TYPE_NAME))));
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInUpdateMethod() throws Exception {
    // when
    mockMvc
        .perform(put(ACCOUNT_TYPE_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(convertAccountTypeToAccountTypeRequest(accountInvestment()))))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateAccountType() throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    long accountTypeId = callRestServiceToAddAccountTypeAndReturnId(accountTypeRequest, token);

    AccountTypeRequest accountTypeToUpdate = AccountTypeRequest.builder()
        .name("AccountCredit")
        .build();

    // when
    mockMvc.perform(put(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeToUpdate)))
        .andExpect(status().isOk());

    // then
    mockMvc.perform(get(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.name", is(accountTypeToUpdate.getName())))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingId() throws Exception {
    // when
    mockMvc
        .perform(get(ACCOUNT_TYPE_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateAccountWithUpdatedAccountSameNameAsBefore() throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    long accountTypeId = callRestServiceToAddAccountTypeAndReturnId(accountTypeRequest, token);
    AccountTypeRequest updatedAccountType = AccountTypeRequest.builder()
        .name(accountTypeRequest.getName()).build();

    mockMvc.perform(put(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccountType)))
        .andExpect(status().isOk());

    mockMvc.perform(get(ACCOUNT_TYPE_SERVICE_PATH + "/" + accountTypeId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.name", is(equalTo(updatedAccountType.getName()))))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

}
