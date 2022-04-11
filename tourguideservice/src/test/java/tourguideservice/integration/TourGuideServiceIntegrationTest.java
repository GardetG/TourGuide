package tourguideservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.PreferencesDto;
import shared.dto.UserDto;
import tourguideservice.proxy.UserServiceProxy;
import tourguideservice.service.tracker.Tracker;

@Tag("integration")
@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.internalUserNumber=3"})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TourGuideServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Tracker tracker;
  @Autowired
  private UserServiceProxy userServiceProxy;

  @BeforeEach
  void setUp() {
    tracker.startTracking();
  }

  @AfterEach()
  void tearUp() {
    tracker.stopTracking();
  }


  @DisplayName("GET user location should return 200 with last location")
  @Test
  void getLocationTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/getLocation?userName=internalUser0"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.longitude").isNotEmpty())
        .andExpect(jsonPath("$.latitude").isNotEmpty());
  }


  @DisplayName("GET all current location should return 200 with list of user and current location")
  @Test
  void getAllCurrentLocations() throws Exception {
    // GIVEN
    UserDto user = userServiceProxy.getUser("internalUser0");

    // WHEN
    mockMvc.perform(get("/getAllCurrentLocations"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + user.getUserId() + ".longitude").isNotEmpty())
        .andExpect(jsonPath("$." + user.getUserId() + ".latitude").isNotEmpty());
  }

  @DisplayName("Set a user preferences should allow to retrieve the updated preferences")
  @Test
  void setAndGetUserPreferencesTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.TEN, 4,3,2,1);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=internalUser0")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/getUserPreferences?userName=internalUser0"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lowerPricePoint", is(0.0)))
        .andExpect(jsonPath("$.highPricePoint", is(10.0)))
        .andExpect(jsonPath("$.tripDuration", is(4)))
        .andExpect(jsonPath("$.ticketQuantity", is(3)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(1)));
  }

  @DisplayName("GET user trip deals should return 200 with list of provider Dto")
  @Test
  void getTripDealsTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/getTripDeals?userName=internalUser1"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));
  }

  @DisplayName("GET nearby attraction should return 200 with nearby attractions")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/getNearbyAttractions?userName=internalUser2"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userLocation.longitude").isNotEmpty())
        .andExpect(jsonPath("$.userLocation.latitude").isNotEmpty())
        .andExpect(jsonPath("$.attractions", hasSize(5)));
  }

}
