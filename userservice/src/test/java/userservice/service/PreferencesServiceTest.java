package userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shared.dto.PreferencesDto;
import shared.exception.UserNotFoundException;
import userservice.domain.User;
import userservice.domain.UserPreferences;

@SpringBootTest
class PreferencesServiceTest {

  @Autowired
  private PreferencesService preferencesService;

  @MockBean
  private UserService userService;

  @Captor
  ArgumentCaptor<UserPreferences> preferencesCaptor;

  @DisplayName("Get user preferences by username should return a preferences Dto")
  @Test
  void getUserPreferencesTest() throws Exception {
    // Given
    User user = new User("jon", "000-000-0000", "jon@test.com");
    UserPreferences preferences = new UserPreferences(10d,100d,4,3,2,1);
    user.setUserPreferences(preferences);
    PreferencesDto expectedPreferences = new PreferencesDto(BigDecimal.valueOf(10d),BigDecimal.valueOf(100d),4,3,2,1);
    when(userService.retrieveUser(anyString())).thenReturn(user);

    // When
    PreferencesDto actualPreferences = preferencesService.getUserPreferences("jon");

    // Then
    assertThat(actualPreferences).usingRecursiveComparison().isEqualTo(expectedPreferences);
    verify(userService, times(1)).retrieveUser("jon");
  }

  @DisplayName("Get user preferences when not found should throw an exception")
  @Test
  void getUserPreferencesNotFoundTest() throws Exception {
    // Given
    when(userService.retrieveUser(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> preferencesService.getUserPreferences("NonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userService, times(1)).retrieveUser("NonExistent");
  }

  @DisplayName("Set user preferences should update user preferences")
  @Test
  void setUserPreferencesTest() throws Exception {
    // Given
    User user = new User("jon", "000-000-0000", "jon@test.com");
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.valueOf(10),BigDecimal.valueOf(100),4,3,2,1);
    UserPreferences expectedPreferences = new UserPreferences(10d,100d,4,3,2,1);
    when(userService.retrieveUser(anyString())).thenReturn(user);

    // When
    preferencesService.setUserPreferences("jon", preferencesDto);

    // Then
    assertThat(user.getUserPreferences()).usingRecursiveComparison().isEqualTo(expectedPreferences);
    verify(userService, times(1)).retrieveUser("jon");
  }

  @DisplayName("Set user preferences with already used username should throw an Exception")
  @Test
  void setUserPreferencesWhenNotFoundTest() throws Exception {
    // Given
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.valueOf(10),BigDecimal.valueOf(100),4,3,2,1);
    when(userService.retrieveUser(anyString())).thenThrow(new UserNotFoundException("This user is not found"));

    // Then
    assertThatThrownBy(() -> preferencesService.setUserPreferences("nonExistent", preferencesDto))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveUser("nonExistent");
  }

}
