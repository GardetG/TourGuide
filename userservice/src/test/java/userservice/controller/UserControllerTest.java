package userservice.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.UserDto;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import userservice.service.PreferencesService;
import userservice.service.UserService;

@WebMvcTest
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;
  @MockBean
  private PreferencesService preferencesService;

  @Captor
  ArgumentCaptor<UserDto> userCaptor;

  @DisplayName("GET user should return 200 with user")
  @Test
  void getUserTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "jon", "000-000-0000", "jon@test.com");
    when(userService.getUser(anyString())).thenReturn(userDto);

    // WHEN
    mockMvc.perform(get("/getUser?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.userName", is("jon")))
        .andExpect(jsonPath("$.phoneNumber", is("000-000-0000")))
        .andExpect(jsonPath("$.emailAddress", is("jon@test.com")));
    verify(userService, times(1)).getUser("jon");
  }

  @DisplayName("GET user when not found should return 404")
  @Test
  void getUserWhenNotFoundTest() throws Exception {
    // GIVEN
    when(userService.getUser(anyString())).thenThrow(new UserNotFoundException("The user is not found"));

    // WHEN
    mockMvc.perform(get("/getUser?userName=NonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("The user is not found")));
    verify(userService, times(1)).getUser("NonExistent");
  }

  @DisplayName("POST user request user registration and should return 200 with user registered")
  @Test
  void addUserTest() throws Exception {
    // GIVEN
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("userName", "jon")
        .put("phoneNumber", "000-000-0000")
        .put("emailAddress", "jon@test.com");
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "jon", "000-000-0000", "jon@test.com");
    when(userService.addUser(any(UserDto.class))).thenReturn(userDto);

    // WHEN
    mockMvc.perform(post("/addUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString()))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.userName", is("jon")))
        .andExpect(jsonPath("$.phoneNumber", is("000-000-0000")))
        .andExpect(jsonPath("$.emailAddress", is("jon@test.com")));
    verify(userService, times(1)).addUser(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().ignoringFields("userId").isEqualTo(userDto);
  }

  @DisplayName("POST user request user registration and should return 200 with user registered")
  @Test
  void addUserWhenUserNameAlreadyUsedTest() throws Exception {
    // GIVEN
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("userName", "jon")
        .put("phoneNumber", "000-000-0000")
        .put("emailAddress", "jon@test.com");
    when(userService.addUser(any(UserDto.class)))
        .thenThrow(new UserNameAlreadyUsedException("This username is already used"));

    // WHEN
    mockMvc.perform(post("/addUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This username is already used")));
    verify(userService, times(1)).addUser(any(UserDto.class));

  }

  @DisplayName("POST invalid user should return 422")
  @Test
  void addUserWhenInvalidTest() throws Exception {
    // GIVEN
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("userName", "")
        .put("phoneNumber", "  ")
        .put("emailAddress", "");

    // WHEN
    mockMvc.perform(post("/addUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString()))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.userName", is("UserName is mandatory")))
        .andExpect(jsonPath("$.phoneNumber", is("Phone number is mandatory")))
        .andExpect(jsonPath("$.emailAddress", is("Email address is mandatory")));
    verify(userService, times(0)).addUser(any(UserDto.class));
  }

  @DisplayName("GET all user id should return 200 with list of ids")
  @Test
  void getAllUserIdTest() throws Exception {
    // GIVEN
    UUID user1Id = UUID.randomUUID();
    UUID user2Id = UUID.randomUUID();
    when(userService.getAllUserId()).thenReturn(List.of(user1Id, user2Id));

    // WHEN
    mockMvc.perform(get("/getAllUserId"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(user1Id.toString())))
        .andExpect(jsonPath("$[1]]", is(user2Id.toString())));
    verify(userService, times(1)).getAllUserId();
  }

}
