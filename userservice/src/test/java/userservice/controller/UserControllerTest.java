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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import shared.dto.PreferencesDto;
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
  @Captor
  ArgumentCaptor<PreferencesDto> preferencesCaptor;

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

  @DisplayName("GET user preferences should return 200 with preferences")
  @Test
  void getUserPreferencesTest() throws Exception {
    // GIVEN
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.TEN, 4,3,2,1);
    when(preferencesService.getUserPreferences(anyString())).thenReturn(preferencesDto);

    // WHEN
    mockMvc.perform(get("/getUserPreferences?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lowerPricePoint", is(0)))
        .andExpect(jsonPath("$.highPricePoint", is(10)))
        .andExpect(jsonPath("$.tripDuration", is(4)))
        .andExpect(jsonPath("$.ticketQuantity", is(3)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(1)));
    verify(preferencesService, times(1)).getUserPreferences("jon");
  }

  @DisplayName("GET user preferences when user not found should return 404")
  @Test
  void getUserPreferencesWhenNotFoundTest() throws Exception {
    // WHEN
    when(preferencesService.getUserPreferences(anyString())).thenThrow(new UserNotFoundException("The user is not found"));

    // THEN
    mockMvc.perform(get("/getUserPreferences?userName=NonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("The user is not found")));
    verify(preferencesService, times(1)).getUserPreferences("NonExistent");
  }

  @DisplayName("PUT user preferences should return 200 with updated preferences")
  @Test
  void setUserPreferences() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.TEN, 4,3,2,1);
    when(preferencesService.setUserPreferences(anyString(), any(PreferencesDto.class))).thenReturn(preferencesDto);
    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=jon")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lowerPricePoint", is(0)))
        .andExpect(jsonPath("$.highPricePoint", is(10)))
        .andExpect(jsonPath("$.tripDuration", is(4)))
        .andExpect(jsonPath("$.ticketQuantity", is(3)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(1)));
    verify(preferencesService, times(1)).setUserPreferences(anyString(), preferencesCaptor.capture());
    assertThat(preferencesCaptor.getValue()).usingRecursiveComparison().isEqualTo(preferencesDto);
  }

  @DisplayName("PUT user preferences when user not found should return 404")
  @Test
  void setUserPreferencesWhenNotFoundTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.TEN, 4,3,2,1);
    when(preferencesService.setUserPreferences(anyString(), any(PreferencesDto.class)))
        .thenThrow(new UserNotFoundException("The user is not found"));

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=NonExistent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("The user is not found")));
    verify(preferencesService, times(1)).setUserPreferences(anyString(), any(PreferencesDto.class));

  }

  @DisplayName("PUT invalid user preferences should return 422")
  @Test
  void setUserPreferencesWhenInvalidTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.valueOf(0), BigDecimal.valueOf(-1), -1, -1, -1, -1);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=jon")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.preferencesDto", is("Lower price point cannot be greater than High price point")))
        .andExpect(jsonPath("$.highPricePoint", is("High price point cannot be negative")))
        .andExpect(jsonPath("$.tripDuration", is("Trip Duration cannot be negative or equals to 0")))
        .andExpect(jsonPath("$.ticketQuantity", is("Ticket Quantity cannot be negative")))
        .andExpect(jsonPath("$.numberOfAdults", is("Number of Adults cannot be negative")))
        .andExpect(jsonPath("$.numberOfChildren", is("Number of Children cannot be negative")));
    verify(userService, times(0)).addUser(any(UserDto.class));
  }

}
