package com.pfm.filter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FilterServiceTest {

  private static final long NOT_EXISTING_ID = 0;
  private static final long MOCK_USER_ID = 99;

  @Mock
  private FilterRepository filterRepository;

  @InjectMocks
  private FilterService filterService;

  @Test
  public void shouldReturnExceptionCausedByIdDoesNotExistInDb() {

    //given
    when(filterRepository.findByIdAndUserId(NOT_EXISTING_ID, MOCK_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> filterService.updateFilter(NOT_EXISTING_ID, MOCK_USER_ID, new Filter()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Filter with id: " + NOT_EXISTING_ID + " does not exist in database")));
  }
}
