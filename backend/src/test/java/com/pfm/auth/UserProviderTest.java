package com.pfm.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserProviderTest {

  @Test
  void shouldThrowExceptionWhenRequestingCurrentUserButUserWasNotSet() {
    // given
    UserProvider userProvider = new UserProvider();

    // when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> {
          userProvider.getCurrentUserId();
        }
    );

    // then
    assertThat(exception.getMessage(), is("No user is logged in but user id was requested"));
  }

  @Test
  void shouldThrowExceptionWhenRequestingCurrentUserButUserWasRemoved() {
    // given
    final long userId = 7432976243L;

    UserProvider userProvider = new UserProvider();
    userProvider.setUser(userId);
    userProvider.removeUser();

    // when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> {
          userProvider.getCurrentUserId();
        }
    );

    // then
    assertThat(exception.getMessage(), is("No user is logged in but user id was requested"));
  }

  @Test
  void shouldReturnUserWhenUserWasSaved() {
    // given
    final long userId = 5324234L;

    UserProvider userProvider = new UserProvider();
    userProvider.setUser(userId);

    // when
    long returnedUser = userProvider.getCurrentUserId();

    // then
    assertThat(returnedUser, is(userId));
  }
}