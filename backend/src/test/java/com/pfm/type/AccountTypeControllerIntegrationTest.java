package com.pfm.type;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.type.AccountTypeRequest;
import com.pfm.helpers.IntegrationTestsBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class AccountTypeControllerIntegrationTest extends IntegrationTestsBase {
  @BeforeEach
  public void setUp() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddAccountType() throws Exception {
    //given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    //when
    String response =
        mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountTypeRequest)))
            .andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString();

    //then
    Long accountTypeId = Long.parseLong(response);

    mockMvc
        .perform(get(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(accountTypeId.intValue())))
        .andExpect(jsonPath("$[0].name", is(accountTypeRequest.getName())))
        .andExpect(jsonPath("$[1].name", is("Credit")))
        .andExpect(jsonPath("$[2].name", is("Investment")))
        .andExpect(jsonPath("$[3].name", is("Personal")))
        .andExpect(jsonPath("$[4].name", is("Saving")))
        .andExpect(jsonPath("$[0].userId").doesNotExist());

    assertThat(accountTypeService.getAccountTypes(userId), hasSize(5));
  }
}
