package com.pfm.filter;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.auth.UserService;
import com.pfm.config.MessagesProvider;
import com.pfm.filters.CorrelationIdFilter;
import com.pfm.helpers.IntegrationTestsBase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class RestExceptionHandlerIntegrationTest extends IntegrationTestsBase {

  @MockBean
  private UserService userService;

  @Test
  public void shouldReceiveUserFriendlyFormattedMessageOnInternalError() throws Exception {
    // given
    when(userService.registerUser(any())).thenThrow(IllegalStateException.class);

    final String correlationId = UUID.randomUUID().toString();
    String expectedMessage = String.format(MessagesProvider.getMessage(MessagesProvider.INTERNAL_ERROR), correlationId,
        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)); // ignoring time as it may differ in seconds / milliseconds
    expectedMessage = expectedMessage.substring(0, expectedMessage.length() - 1); // remove "

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .header(CorrelationIdFilter.CORRELATION_ID, correlationId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(userMarian())))

        // then
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$", containsString(expectedMessage)));

  }

  @Test
  public void shouldReceiveUserFriendlyFormattedMessageOnBadRequestError() throws Exception {
    // given

    final String correlationId = UUID.randomUUID().toString();
    String expectedMessage = String.format(MessagesProvider.getMessage(MessagesProvider.BAD_REQUEST), correlationId,
        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)); // ignoring time as it may differ in seconds / milliseconds
    expectedMessage = expectedMessage.substring(0, expectedMessage.length() - 1); // remove "

    String body = json(userMarian());
    body = body.replace("username", "username123"); // no such field exists so bad request will be returned
    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .header(CorrelationIdFilter.CORRELATION_ID, correlationId)
        .contentType(JSON_CONTENT_TYPE)
        .content(body))

        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", containsString(expectedMessage)));

  }
}
