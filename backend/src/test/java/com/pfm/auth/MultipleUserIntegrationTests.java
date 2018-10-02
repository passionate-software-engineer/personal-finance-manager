package com.pfm.auth;

import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import org.junit.Test;

public class MultipleUserIntegrationTests extends IntegrationTestsBase {

  @Test
  public void shouldReturnUnauthorizedCausedByWrongToken() throws Exception {

    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .header("Authorization", "Wrong token")
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void shouldReturnUnauthorizedCausedByEmptyToken() throws Exception {

    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .header("Authorization", "")
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void shouldReturnUnauthorizedCausedByNullToken() throws Exception {

    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void optionsRequestTest() throws Exception {
    mockMvc
        .perform(options(ACCOUNTS_SERVICE_PATH))
        .andExpect(status().isOk());
  }

}
