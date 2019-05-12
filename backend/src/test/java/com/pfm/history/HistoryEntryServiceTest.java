package com.pfm.history;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pfm.account.Account;
import com.pfm.category.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HistoryEntryServiceTest {

  @Mock
  private HistoryEntryRepository historyEntryRepository;

  @Mock
  private HistoryInfoProvider historyInfoProvider;

  @InjectMocks
  private HistoryEntryService historyEntryService;

  @Test
  public void shouldThrowExceptionCausedByTwoArgumentsOfDifferentTypes() {
    //given
    Account account = new Account();
    Category category = new Category();

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      historyEntryService.addHistoryEntryOnUpdate(category, account, 1L);
    });

    // then
    assertThat(exception.getMessage(), is("Parameters oldObject and newObject are not the same types"));
  }

}
