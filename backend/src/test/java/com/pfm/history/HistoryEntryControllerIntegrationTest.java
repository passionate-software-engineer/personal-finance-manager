package com.pfm.history;

import static com.pfm.helpers.TestUsersProvider.userMarian;

import com.pfm.helpers.IntegrationTestsBase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class HistoryEntryControllerIntegrationTest extends IntegrationTestsBase {

  private static final String HISTORY_PATH = "/history";

  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldGetAllHistoryEntries() throws Exception {

  }

  private List<List<String>> callRestServiceToReturnHistoryEntry(String token) throws Exception {
    //    String response =
    //        mockMvc
    //            .perform(get(HISTORY_PATH)
    //                .header(HttpHeaders.AUTHORIZATION, token))
    //            .andExpect(status().isOk())
    //            .andReturn().getResponse().getContentAsString();
    //
    //    List<HistoryEntry> historyEntriesFromResponse = getHistoryEntriesFromResponse(response);
    //
    //    return historyEntriesFromResponse.stream()
    //        .map(historyEntry -> historyEntry.getEntry())
    //        .collect(Collectors.toList());
    return null;
  }

  private List<HistoryEntry> getHistoryEntriesFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, HistoryEntry.class));
  }
}