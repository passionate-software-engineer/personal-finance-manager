package com.pfm.auth;

import static com.pfm.config.MessagesProvider.getMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import com.pfm.config.MessagesProvider;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

  @InjectMocks
  private UserValidator validator;

  @Mock
  private UserService userService;

  @ParameterizedTest
  @ValueSource(ints = {256, 257, 280, 300, 1000, 51341})
  public void shouldReturnUsernameTooLongErrorWhenTooLongUsernameWasProvided(int usernameLength) {
    // given
    User user = User.builder()
        .username("a".repeat(usernameLength))
        .firstName("Tom")
        .lastName("Cruiser")
        .password("MySecretPassword")
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(String.format(getMessage(MessagesProvider.TOO_LONG_USERNAME), UserValidator.USERNAME_MAX_LENGTH)));
  }

  @ParameterizedTest
  @ValueSource(ints = {255, 254, 250, 100, 10, 1})
  public void shouldNotReturnUsernameTooLongErrorWhenCorrectUserNameWasProvided(int usernameLength) {
    // given
    User user = User.builder()
        .username("X".repeat(usernameLength))
        .firstName("Tomasz")
        .lastName("Kruszynski")
        .password("Abc123!")
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {256, 257, 280, 300, 1000, 51341})
  public void shouldReturnFirstNameTooLongErrorWhenTooLongFirstNameWasProvided(int firstNameLength) {
    // given
    User user = User.builder()
        .username("Greg")
        .firstName("T".repeat(firstNameLength))
        .lastName("Kowalski")
        .password("XYZ")
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(String.format(getMessage(MessagesProvider.TOO_LONG_FIRST_NAME), UserValidator.FIRST_NAME_MAX_LENGTH)));
  }

  @ParameterizedTest
  @ValueSource(ints = {255, 254, 250, 100, 10, 1})
  public void shouldNotReturnFirstNameTooLongErrorWhenCorrectFirstNameWasProvided(int firstNameLength) {
    // given
    User user = User.builder()
        .username("Greg")
        .firstName("T".repeat(firstNameLength))
        .lastName("Kowalski")
        .password("XYZ")
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {256, 257, 280, 300, 1000, 51341})
  public void shouldReturnLastNameTooLongErrorWhenTooLongLastNameWasProvided(int lastNameLength) {
    // given
    User user = User.builder()
        .username("Greg")
        .firstName("Gregory")
        .lastName("U".repeat(lastNameLength))
        .password("XYZ123")
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(String.format(getMessage(MessagesProvider.TOO_LONG_LAST_NAME), UserValidator.LAST_NAME_MAX_LENGTH)));
  }

  @ParameterizedTest
  @ValueSource(ints = {255, 254, 250, 100, 10, 1})
  public void shouldNotReturnLastNameTooLongErrorWhenCorrectLastNameWasProvided(int lastNameLength) {
    // given
    User user = User.builder()
        .username("Greg")
        .firstName("Gregory")
        .lastName("U".repeat(lastNameLength))
        .password("XYZ123")
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {256, 257, 280, 300, 1000, 51341})
  public void shouldReturnPasswordTooLongErrorWhenTooLongPasswordWasProvided(int passwordLength) {
    // given
    User user = User.builder()
        .username("Greg")
        .firstName("Gregory")
        .lastName("Liskowski")
        .password("A".repeat(passwordLength))
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(String.format(getMessage(MessagesProvider.TOO_LONG_PASSWORD), UserValidator.PASSWORD_MAX_LENGTH)));
  }

  @ParameterizedTest
  @ValueSource(ints = {255, 254, 250, 100, 10, 1})
  public void shouldNotReturnPasswordTooLongErrorWhenCorrectPasswordWasProvided(int passwordLength) {
    // given
    User user = User.builder()
        .username("Greg")
        .firstName("Gregory")
        .lastName("Liskowski")
        .password("B".repeat(passwordLength))
        .build();

    // when
    List<String> result = validator.validateUser(user);

    // then
    assertThat(result, hasSize(0));
  }
}