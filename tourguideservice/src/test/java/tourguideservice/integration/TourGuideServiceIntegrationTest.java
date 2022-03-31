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
import tourguideservice.service.tracker.Tracker;

@Tag("integration")
@SpringBootTest(properties = "tourguide.internaluser.internalUserNumber=1")
@ActiveProfiles({"test", "internalUser"})
@AutoConfigureMockMvc
class TourGuideServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Tracker tracker;

  @BeforeEach
  void setUp() {
    tracker.startTracking();
  }

  @AfterEach()
  void tearUp() {
    tracker.stopTracking();
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

}
