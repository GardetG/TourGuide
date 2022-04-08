package userservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.PreferencesDto;
import shared.dto.UserDto;
import userservice.domain.User;
import userservice.repository.UserRepository;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  private void setUp() {
    userRepository.deleteAll();
    User user = new User("test", "000-000-0000", "test@test.com");
    userRepository.save(user);
  }

  @DisplayName("Add a user to the service should allow to retrieve it")
  @Test
  void AddAndRetrieveUserTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UserDto userDto = new UserDto(null, "jon", "000-000-0000", "jon@mail.com");

    // WHEN
    mockMvc.perform(post("/addUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/getUser?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName", is("jon")))
        .andExpect(jsonPath("$.phoneNumber", is("000-000-0000")))
        .andExpect(jsonPath("$.emailAddress", is("jon@mail.com")))
        .andExpect(jsonPath("$.userId").isNotEmpty());
  }

  @DisplayName("Get all user id should return list of all user id registered")
  @Test
  void getAllUserId() throws Exception {
    // WHEN
    mockMvc.perform(get("/getAllUserId"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @DisplayName("Set a user preferences should allow to retrieve the updated preferences")
  @Test
  void setAndGetUserPreferencesTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.TEN, 4,3,2,1);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/getUserPreferences?userName=test"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lowerPricePoint", is(0.0)))
        .andExpect(jsonPath("$.highPricePoint", is(10.0)))
        .andExpect(jsonPath("$.tripDuration", is(4)))
        .andExpect(jsonPath("$.ticketQuantity", is(3)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(1)));
  }

}
