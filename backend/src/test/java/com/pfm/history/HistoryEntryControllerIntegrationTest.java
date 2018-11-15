package com.pfm.history;

import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestHelper;
import com.pfm.history.HistoryEntry.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class HistoryEntryControllerIntegrationTest extends IntegrationTestsBase {

  private static final String HISTORY_PATH = "/history";

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldReturnHistoryOfAddingAccount() throws Exception {

    //given
    Account account = accountMbankBalance10();
    callRestServiceToAddAccountAndReturnId(account, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(1L)
        .name("name")
        .newValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(2L)
        .name("balance")
        .newValue(account.getBalance().toString())
        .build());

    assertThat(historyEntries, hasSize(1));
    assertThat(historyEntries.get(0).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(0).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingAccount() throws Exception {

    //given
    Account account = accountMbankBalance10();
    Account updatedAccount = accountMbankBalance10();
    updatedAccount.setName("updatedName");
    updatedAccount.setBalance(TestHelper.convertDoubleToBigDecimal(999));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToUpdateAccount(accountId, convertAccountToAccountRequest(updatedAccount), token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(3L)
        .name("name")
        .newValue(updatedAccount.getName())
        .oldValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("balance")
        .newValue(updatedAccount.getBalance().toString())
        .oldValue(account.getBalance().toString())
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfDeletingAccount() throws Exception {

    //given
    Account account = accountMbankBalance10();

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToDeleteAccountById(accountId, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(3L)
        .name("name")
        .oldValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("balance")
        .oldValue(account.getBalance().toString())
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  private List<HistoryEntry> callRestServiceToReturnHistoryEntries(String token) throws Exception {
    String response =
        mockMvc
            .perform(get(HISTORY_PATH)
                .header(HttpHeaders.AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    return getHistoryEntriesFromResponse(response);
  }

  private List<List<HistoryInfo>> callRestServiceToReturnHistoryInfos(String token) throws Exception {
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);
    return historyEntries.stream()
        .map(HistoryEntry::getEntries)
        .collect(Collectors.toList());

  }

  private List<HistoryEntry> getHistoryEntriesFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, HistoryEntry.class));
  }


}