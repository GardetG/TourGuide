package tourguideservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tourguideservice.domain.User;
import tourguideservice.repository.UserRepository;
import tourguideservice.service.tracker.Tracker;

@Tag("integration")
@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.internalUserNumber=1"})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TourGuideServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Tracker tracker;
  @Autowired
  private UserRepository userRepository;

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
    User user = userRepository.findByUsername("internalUser0").orElseThrow();

    // WHEN
    mockMvc.perform(get("/getAllCurrentLocations"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + user.getUserId() + ".longitude").isNotEmpty())
        .andExpect(jsonPath("$." + user.getUserId() + ".latitude").isNotEmpty());
  }

  @DisplayName("GET user trip deals should return 200 with list of provider Dto")
  @Test
  void getTripDealsTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/getTripDeals?userName=internalUser0"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));
  }

  @DisplayName("GET nearby attraction should return 200 with nearby attractions")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/getNearbyAttractions?userName=internalUser0"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userLocation.longitude").isNotEmpty())
        .andExpect(jsonPath("$.userLocation.latitude").isNotEmpty())
        .andExpect(jsonPath("$.attractions", hasSize(5)));
  }

}
